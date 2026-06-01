package com.framework.subcriptions.controller;

import com.framework.subcriptions.domain.Currency;
import com.framework.subcriptions.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class RatesController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ExchangeRateService exchangeRateService;

    @GetMapping("/rates")
    public String rates(Model model) {
        LocalDateTime lastUpdated = exchangeRateService.getLastUpdated();

        model.addAttribute("title", "현재 환율");
        model.addAttribute("usdRate", String.format("%,.2f", exchangeRateService.getRate(Currency.USD)));
        model.addAttribute("jpyRate", String.format("%,.2f", exchangeRateService.getRate(Currency.JPY)));
        model.addAttribute("realTime", exchangeRateService.isRealTime());
        model.addAttribute("lastUpdated", lastUpdated != null ? lastUpdated.format(FMT) : null);
        model.addAttribute("lastError", exchangeRateService.getLastError());
        return "rates";
    }

    // 수동 갱신: 캐시를 즉시 재요청하고 결과를 확인할 수 있도록 /rates로 리다이렉트.
    @PostMapping("/rates/refresh")
    public String refresh() {
        exchangeRateService.refreshRates();
        return "redirect:/rates";
    }
}
