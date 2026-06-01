package com.framework.subcriptions.service;

import com.framework.subcriptions.domain.Currency;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ExchangeRateService {

    private static final String API_URL = "https://api.frankfurter.app/latest?from=USD&to=KRW,JPY";

    private final RestClient restClient = buildRestClient();
    private final Map<Currency, BigDecimal> ratesToKrw = new ConcurrentHashMap<>();

    private volatile LocalDateTime lastUpdated = null;
    private volatile String lastError = null;

    // text/html로 응답이 와도 Jackson이 파싱 시도하도록 허용 (일부 CDN·프록시가 Content-Type을 잘못 설정함).
    private static RestClient buildRestClient() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));
        return RestClient.builder()
                .messageConverters(converters -> {
                    converters.removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
                    converters.add(converter);
                })
                .build();
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
            FrankfurterResponse response = restClient.get()
                    .uri(API_URL)
                    .header("Accept", "application/json")
                    .retrieve()
                    .body(FrankfurterResponse.class);

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

    // Jackson 역직렬화를 위해 package-private으로 선언 (private이면 생성자 접근 불가).
    record FrankfurterResponse(String base, String date, Map<String, BigDecimal> rates) {}
}
