package com.framework.subcriptions.service;

import com.framework.subcriptions.domain.Currency;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ExchangeRateService {

    // open.er-api.com: 무료, API 키 불필요, KRW 포함 170개 통화 지원.
    // Frankfurter(ECB 데이터)는 KRW 미지원 → HTML 에러 페이지 반환 문제로 교체.
    private static final String API_URL = "https://open.er-api.com/v6/latest/USD";

    private final RestClient restClient;
    private final Map<Currency, BigDecimal> ratesToKrw = new ConcurrentHashMap<>();

    private volatile LocalDateTime lastUpdated = null;
    private volatile String lastError = null;

    // Spring Boot 자동 구성 RestClient.Builder 주입 → ObjectMapper 설정(unknown property 무시 등) 상속.
    public ExchangeRateService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @PostConstruct
    public void init() {
        for (Currency c : Currency.values()) {
            ratesToKrw.put(c, c.getToKrwRate());
        }
        refreshRates();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void refreshRates() {
        try {
            ExchangeRateResponse response = restClient.get()
                    .uri(API_URL)
                    .header("Accept", "application/json")
                    .retrieve()
                    .body(ExchangeRateResponse.class);

            if (response == null || response.rates() == null) {
                lastError = "API 응답이 비어있음";
                log.warn("환율 API 응답이 비어있음. fallback 환율 유지");
                return;
            }

            BigDecimal krwPerUsd = response.rates().get("KRW");
            BigDecimal jpyPerUsd = response.rates().get("JPY");

            if (krwPerUsd != null && krwPerUsd.compareTo(BigDecimal.ZERO) > 0) {
                ratesToKrw.put(Currency.USD, krwPerUsd);
            }
            if (krwPerUsd != null && krwPerUsd.compareTo(BigDecimal.ZERO) > 0
                    && jpyPerUsd != null && jpyPerUsd.compareTo(BigDecimal.ZERO) > 0) {
                ratesToKrw.put(Currency.JPY,
                        krwPerUsd.divide(jpyPerUsd, 10, RoundingMode.HALF_UP));
            }
            ratesToKrw.put(Currency.KRW, BigDecimal.ONE);

            lastUpdated = LocalDateTime.now();
            lastError = null;
            log.info("환율 갱신 완료: USD={}, JPY={}",
                    ratesToKrw.get(Currency.USD), ratesToKrw.get(Currency.JPY));
        } catch (Exception e) {
            lastError = e.getClass().getSimpleName() + ": " + e.getMessage();
            log.warn("환율 API 호출 실패. fallback 환율 유지: {}", e.getMessage());
        }
    }

    public BigDecimal getRate(Currency currency) {
        return ratesToKrw.getOrDefault(currency, currency.getToKrwRate());
    }

    public int toKrw(Currency currency, int amount) {
        return getRate(currency)
                .multiply(BigDecimal.valueOf(amount))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }

    public boolean isRealTime() {
        return lastUpdated != null;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public String getLastError() {
        return lastError;
    }

    record ExchangeRateResponse(Map<String, BigDecimal> rates) {}
}
