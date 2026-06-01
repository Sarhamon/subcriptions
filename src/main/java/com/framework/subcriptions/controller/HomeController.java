package com.framework.subcriptions.controller;

import com.framework.subcriptions.dto.SubscriptionView;
import com.framework.subcriptions.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/")
    public String home(Model model) {
        List<SubscriptionView> all = subscriptionService.findAll();
        LocalDate today = LocalDate.now();

        int totalMonthlyKrw = all.stream().mapToInt(SubscriptionView::getMonthlyKrw).sum();
        long autoCount      = all.stream().filter(SubscriptionView::isAutoRenew).count();
        long thisMonthCount = all.stream()
                .filter(v -> v.getNextRenewalDate().getMonthValue() == today.getMonthValue()
                          && v.getNextRenewalDate().getYear()       == today.getYear())
                .count();
        long soonCount = all.stream().filter(SubscriptionView::isDueSoon7).count();

        List<SubscriptionView> upcomingList = all.stream()
                .sorted(Comparator.comparing(SubscriptionView::getNextRenewalDate))
                .limit(4)
                .toList();

        String todayLabel = String.format("%d년 %d월 %d일 기준 · 원화 환산",
                today.getYear(), today.getMonthValue(), today.getDayOfMonth());

        model.addAttribute("title", "구독 현황");
        model.addAttribute("subscriptions", all);
        model.addAttribute("upcomingList", upcomingList);
        model.addAttribute("totalMonthlyKrw", String.format("%,d", totalMonthlyKrw));
        model.addAttribute("totalCount", all.size() + "개");
        model.addAttribute("autoCount", "자동결제 " + autoCount + "개");
        model.addAttribute("thisMonthCount", thisMonthCount + "건");
        model.addAttribute("soonCount", "7일 이내 " + soonCount + "건");
        model.addAttribute("todayLabel", todayLabel);

        return "index";
    }
}
