package com.framework.subcriptions.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public enum Currency {
    KRW("원", new BigDecimal("1")),
    JPY("엔", new BigDecimal("9.45")),
    USD("달러", new BigDecimal("1498"));

    private final String label;
    private final BigDecimal toKrwRate;

    Currency(String label, BigDecimal toKrwRate) {
        this.label = label;
        this.toKrwRate = toKrwRate;
    }

    public int toKrw(int amount) {
        return toKrwRate.multiply(BigDecimal.valueOf(amount))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }
}
