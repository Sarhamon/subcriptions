package com.framework.subcriptions.dto;

import com.framework.subcriptions.domain.BillingCycle;
import com.framework.subcriptions.domain.Subscription;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class SubscriptionForm {

    private String serviceName;
    private Integer price;
    private BillingCycle billingCycle;
    private LocalDate startedAt;
    private boolean autoRenew;

    public Subscription toEntity() {
        return Subscription.builder()
                .serviceName(serviceName)
                .price(price)
                .billingCycle(billingCycle)
                .startedAt(startedAt)
                .autoRenew(autoRenew)
                .build();
    }
}
