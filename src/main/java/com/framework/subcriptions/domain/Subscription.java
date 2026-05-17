package com.framework.subcriptions.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// 구독 1건을 표현하는 JPA 엔티티. 빌더로만 생성하도록 모든 생성자를 캡슐화.
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Subscription {

    // DB가 채우는 PK (H2 IDENTITY → auto increment).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 서비스명 (예: Netflix, Spotify). NOT NULL.
    @Column(nullable = false)
    private String serviceName;

    // 금액. 통화 단위는 currency 필드가 별도로 보관.
    @Column(nullable = false)
    private Integer price;

    // 통화 enum. 문자열로 저장해 ORDINAL 변경 위험을 회피.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    // 결제 주기 enum (MONTHLY/YEARLY). 역시 문자열로 저장.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingCycle billingCycle;

    // 최초 시작일 또는 마지막 갱신일. slideToNextCycle로 갱신된다.
    @Column(nullable = false)
    private LocalDate startedAt;

    // 자동결제 여부. (현 단계에서는 표시용)
    @Column(nullable = false)
    private boolean autoRenew;

    // 다음 갱신일 계산. startedAt + 결제 주기.
    public LocalDate getNextRenewalDate() {
        return billingCycle.addTo(startedAt);
    }

    // startedAt을 다음 갱신일로 한 주기 밀어준다 (자동 갱신 처리에 사용).
    public void slideToNextCycle() {
        this.startedAt = getNextRenewalDate();
    }

    // 폼에서 받은 값으로 엔티티 상태를 갱신. setter 노출 대신 의도가 드러나는 메서드 제공.
    public void updateDetails(String serviceName, Integer price, Currency currency,
                              BillingCycle billingCycle, LocalDate startedAt, boolean autoRenew) {
        this.serviceName = serviceName;
        this.price = price;
        this.currency = currency;
        this.billingCycle = billingCycle;
        this.startedAt = startedAt;
        this.autoRenew = autoRenew;
    }
}
