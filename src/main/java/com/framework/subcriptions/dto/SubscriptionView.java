package com.framework.subcriptions.dto;

import com.framework.subcriptions.domain.BillingCycle;
import com.framework.subcriptions.domain.Currency;
import com.framework.subcriptions.domain.Subscription;
import com.framework.subcriptions.service.ExchangeRateService;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
    private final long daysUntilRenewal;
    private final boolean dueSoon;   // 3일 이내
    private final boolean dueSoon7;  // 7일 이내 (홈 요약 지표용)
    private final String daysText;   // "오늘" | "N일 후"
    private final String colorClass; // c1~c4 (카드 상단 스트라이프)
    private final String emoji;      // 서비스 아이콘
    private final int monthlyKrw;    // 월 환산 원화 (연간 ÷ 12, 홈 지표용)

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
        this.daysUntilRenewal = ChronoUnit.DAYS.between(LocalDate.now(), this.nextRenewalDate);
        this.dueSoon = this.daysUntilRenewal <= 3;
        this.dueSoon7 = this.daysUntilRenewal <= 7;
        this.daysText = this.daysUntilRenewal == 0 ? "오늘" : this.daysUntilRenewal + "일 후";
        this.colorClass = resolveColorClass(s.getId());
        this.emoji = resolveEmoji(s.getServiceName());
        this.monthlyKrw = calcMonthlyKrw(s, rateService);
    }

    private static String buildDisplayPrice(Subscription s, ExchangeRateService rateService) {
        String main = String.format("%,d %s", s.getPrice(), s.getCurrency().getLabel());
        if (s.getCurrency() == Currency.KRW) {
            return main;
        }
        int krw = rateService.toKrw(s.getCurrency(), s.getPrice());
        return main + " (" + String.format("%,d", krw) + "원)";
    }

    private static String resolveColorClass(Long id) {
        String[] classes = {"c1", "c2", "c3", "c4"};
        return classes[(int) Math.abs((id - 1) % 4)];
    }

    private static String resolveEmoji(String name) {
        String n = name.toLowerCase();
        if (n.contains("netflix"))   return "🎬";
        if (n.contains("spotify"))   return "🎵";
        if (n.contains("youtube"))   return "▶️";
        if (n.contains("claude"))    return "🤖";
        if (n.contains("notion"))    return "📝";
        if (n.contains("chatgpt"))   return "💬";
        if (n.contains("nintendo"))  return "🎮";
        return "📦";
    }

    private static int calcMonthlyKrw(Subscription s, ExchangeRateService rateService) {
        int krw = rateService.toKrw(s.getCurrency(), s.getPrice());
        return s.getBillingCycle() == BillingCycle.YEARLY ? Math.round(krw / 12f) : krw;
    }
}
