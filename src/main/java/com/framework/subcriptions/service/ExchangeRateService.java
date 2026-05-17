package com.framework.subcriptions.service;

import com.framework.subcriptions.domain.Currency;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 외부 환율 API를 호출해 통화별 "1단위 → 원" 환율을 캐시한다. 실패 시 enum의 fallback 사용.
@Slf4j
@Service
public class ExchangeRateService {

    // Frankfurter 무료 API: KRW 기준의 USD/JPY 환율을 가져온다.
    private static final String API_URL = "https://api.frankfurter.app/latest?from=KRW&to=USD,JPY";

    private final RestClient restClient = RestClient.create();
    // 통화 → 1단위당 원화 환산 비율. 스레드 안전한 캐시.
    private final Map<Currency, BigDecimal> ratesToKrw = new ConcurrentHashMap<>();

    // 빈 초기화 시점에 fallback 환율로 우선 채운 뒤, 실제 API를 1회 호출.
    @PostConstruct
    public void init() {
        for (Currency c : Currency.values()) {
            ratesToKrw.put(c, c.getToKrwRate());
        }
        refreshRates();
    }

    // 매일 자정 자동 갱신. 응답 형식이 KRW당 외화이므로 invert로 뒤집어 저장한다.
    @Scheduled(cron = "0 0 0 * * *")
    public void refreshRates() {
        try {
            FrankfurterResponse response = restClient.get()
                    .uri(API_URL)
                    .retrieve()
                    .body(FrankfurterResponse.class);

            if (response == null || response.rates() == null) {
                log.warn("환율 API 응답이 비어있음. fallback 환율 유지");
                return;
            }

            BigDecimal usdPerKrw = response.rates().get("USD");
            BigDecimal jpyPerKrw = response.rates().get("JPY");

            // 유효한 값(0 초과)만 캐시에 반영. 비정상 값은 무시하고 직전 환율을 유지.
            if (usdPerKrw != null && usdPerKrw.compareTo(BigDecimal.ZERO) > 0) {
                ratesToKrw.put(Currency.USD, invert(usdPerKrw));
            }
            if (jpyPerKrw != null && jpyPerKrw.compareTo(BigDecimal.ZERO) > 0) {
                ratesToKrw.put(Currency.JPY, invert(jpyPerKrw));
            }
            ratesToKrw.put(Currency.KRW, BigDecimal.ONE);

            log.info("환율 갱신 완료: USD={}, JPY={}",
                    ratesToKrw.get(Currency.USD), ratesToKrw.get(Currency.JPY));
        } catch (Exception e) {
            log.warn("환율 API 호출 실패. fallback 환율 유지: {}", e.getMessage());
        }
    }

    // 캐시에서 환율 조회. 누락 시 enum의 fallback으로 안전하게 폴백.
    public BigDecimal getRate(Currency currency) {
        return ratesToKrw.getOrDefault(currency, currency.getToKrwRate());
    }

    // 주어진 통화·금액을 원화로 환산(반올림). SubscriptionView가 화면 표기에 사용.
    public int toKrw(Currency currency, int amount) {
        return getRate(currency)
                .multiply(BigDecimal.valueOf(amount))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }

    // API가 "KRW당 외화"로 주기 때문에 "외화 1단위당 원"으로 뒤집어 저장하기 위한 헬퍼.
    private BigDecimal invert(BigDecimal rate) {
        return BigDecimal.ONE.divide(rate, 10, RoundingMode.HALF_UP);
    }

    // Frankfurter 응답 매핑용 record. base/date는 사용하지 않지만 형태 보존을 위해 유지.
    private record FrankfurterResponse(String base, String date, Map<String, BigDecimal> rates) {
    }
}
