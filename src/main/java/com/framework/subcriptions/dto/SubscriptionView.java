package com.framework.subcriptions.dto;

import com.framework.subcriptions.domain.BillingCycle;
import com.framework.subcriptions.domain.Currency;
import com.framework.subcriptions.domain.Subscription;
import com.framework.subcriptions.service.ExchangeRateService;
import lombok.Getter;

import java.time.LocalDate;

// 화면 출력용 읽기 전용 DTO. 환율 환산 결과(displayPrice)를 미리 계산해 템플릿을 단순하게 유지.
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

    // 엔티티 + 환율 서비스에서 화면용 값을 즉시 채워 불변 객체로 만든다.
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

    // 표시 가격 문자열을 만든다. 외화면 괄호 안에 원화 환산값을 덧붙인다.
    private static String buildDisplayPrice(Subscription s, ExchangeRateService rateService) {
        String main = String.format("%,d %s", s.getPrice(), s.getCurrency().getLabel());
        if (s.getCurrency() == Currency.KRW) {
            return main;
        }
        int krw = rateService.toKrw(s.getCurrency(), s.getPrice());
        return main + " (" + String.format("%,d", krw) + "원)";
    }
}
