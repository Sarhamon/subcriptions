package com.framework.subcriptions.domain;

import lombok.Getter;

import java.time.LocalDate;

// 결제 주기 enum. 화면 표시 라벨과 "주어진 날짜에 한 주기 더하기" 로직을 함께 가진다.
@Getter
public enum BillingCycle {
    MONTHLY("월 갱신"),
    YEARLY("연 갱신");

    // 화면 표시용 한국어 라벨.
    private final String label;

    BillingCycle(String label) {
        this.label = label;
    }

    // 입력 날짜에 자신의 주기를 더해 다음 갱신일을 반환한다.
    public LocalDate addTo(LocalDate from) {
        return switch (this) {
            case MONTHLY -> from.plusMonths(1);
            case YEARLY -> from.plusYears(1);
        };
    }
}
