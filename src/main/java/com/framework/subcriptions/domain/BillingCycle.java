package com.framework.subcriptions.domain;

import java.time.LocalDate;

public enum BillingCycle {
    MONTHLY,
    YEARLY;

    public LocalDate addTo(LocalDate from) {
        return switch (this) {
            case MONTHLY -> from.plusMonths(1);
            case YEARLY -> from.plusYears(1);
        };
    }
}
