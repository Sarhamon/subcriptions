package com.framework.subcriptions.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingCycle billingCycle;

    @Column(nullable = false)
    private LocalDate startedAt;

    @Column(nullable = false)
    private boolean autoRenew;

    public LocalDate getNextRenewalDate() {
        return billingCycle.addTo(startedAt);
    }

    public void slideToNextCycle() {
        this.startedAt = getNextRenewalDate();
    }

    public void updateDetails(String serviceName, Integer price, BillingCycle billingCycle,
                              LocalDate startedAt, boolean autoRenew) {
        this.serviceName = serviceName;
        this.price = price;
        this.billingCycle = billingCycle;
        this.startedAt = startedAt;
        this.autoRenew = autoRenew;
    }
}
