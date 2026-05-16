package com.framework.subcriptions.dto;

import com.framework.subcriptions.domain.BillingCycle;
import com.framework.subcriptions.domain.Currency;
import com.framework.subcriptions.domain.Subscription;
import com.framework.subcriptions.service.ExchangeRateService;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SubscriptionView {

    private final Long id;
    private final String serviceName;
    private final Integer price;
    private final Currency currency;
    private final BillingCycle billingCycle;
    private final LocalDate startedAt;
    private final LocalDate nextRenewalDate;
    private final boolean autoRenew;
    private final String displayPrice;

    public SubscriptionView(Subscription s, ExchangeRateService rateService) {
        this.id = s.getId();
        this.serviceName = s.getServiceName();
        this.price = s.getPrice();
        this.currency = s.getCurrency();
        this.billingCycle = s.getBillingCycle();
        this.startedAt = s.getStartedAt();
        this.nextRenewalDate = s.getNextRenewalDate();
        this.autoRenew = s.isAutoRenew();
        this.displayPrice = buildDisplayPrice(s, rateService);
    }

    private static String buildDisplayPrice(Subscription s, ExchangeRateService rateService) {
        String main = String.format("%,d %s", s.getPrice(), s.getCurrency().getLabel());
        if (s.getCurrency() == Currency.KRW) {
            return main;
        }
        int krw = rateService.toKrw(s.getCurrency(), s.getPrice());
        return main + " (" + String.format("%,d", krw) + "원)";
    }
}
