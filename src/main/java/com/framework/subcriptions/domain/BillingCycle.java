package com.framework.subcriptions.domain;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public enum BillingCycle {
    MONTHLY("월 갱신"),
    YEARLY("연 갱신");

    private final String label;

    BillingCycle(String label) {
        this.label = label;
    }

    public LocalDate addTo(LocalDate from) {
        return switch (this) {
            case MONTHLY -> from.plusMonths(1);
            case YEARLY -> from.plusYears(1);
        };
    }
}
