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

@Slf4j
@Service
public class ExchangeRateService {

    private static final String API_URL = "https://api.frankfurter.app/latest?from=KRW&to=USD,JPY";

    private final RestClient restClient = RestClient.create();
    private final Map<Currency, BigDecimal> ratesToKrw = new ConcurrentHashMap<>();

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
                    .retrieve()
                    .body(FrankfurterResponse.class);

            if (response == null || response.rates() == null) {
                log.warn("환율 API 응답이 비어있음. fallback 환율 유지");
                return;
            }

            BigDecimal usdPerKrw = response.rates().get("USD");
            BigDecimal jpyPerKrw = response.rates().get("JPY");

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

    public BigDecimal getRate(Currency currency) {
        return ratesToKrw.getOrDefault(currency, currency.getToKrwRate());
    }

    public int toKrw(Currency currency, int amount) {
        return getRate(currency)
                .multiply(BigDecimal.valueOf(amount))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }

    private BigDecimal invert(BigDecimal rate) {
        return BigDecimal.ONE.divide(rate, 10, RoundingMode.HALF_UP);
    }

    private record FrankfurterResponse(String base, String date, Map<String, BigDecimal> rates) {
    }
}
