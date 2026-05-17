package com.framework.subcriptions.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

// 통화 enum. 라벨과 함께 API 실패 시 쓸 fallback 환율(1단위 → 원)을 가지고 있다.
@Getter
public enum Currency {
    KRW("원", new BigDecimal("1")),
    JPY("엔", new BigDecimal("9.45")),
    USD("달러", new BigDecimal("1498"));

    // 화면 표시용 라벨.
    private final String label;
    // 1통화 단위가 몇 원인지에 대한 폴백 환율 (실시간 환율은 ExchangeRateService가 덮어씀).
    private final BigDecimal toKrwRate;

    Currency(String label, BigDecimal toKrwRate) {
        this.label = label;
        this.toKrwRate = toKrwRate;
    }

    // 폴백 환율 기준 원화 환산(반올림). 평소엔 ExchangeRateService.toKrw를 사용해야 정확함.
    public int toKrw(int amount) {
        return toKrwRate.multiply(BigDecimal.valueOf(amount))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }
}
