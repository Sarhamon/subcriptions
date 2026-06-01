package com.framework.subcriptions.controller;

import com.framework.subcriptions.domain.Currency;
import com.framework.subcriptions.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class RatesController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping("/rates")
    public String rates(Model model) {
        model.addAttribute("title", "현재 환율");
        model.addAttribute("usdRate", String.format("%,.2f", exchangeRateService.getRate(Currency.USD)));
        model.addAttribute("jpyRate", String.format("%,.2f", exchangeRateService.getRate(Currency.JPY)));
        return "rates";
    }
}
