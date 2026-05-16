package com.framework.subcriptions.controller;

import com.framework.subcriptions.domain.BillingCycle;
import com.framework.subcriptions.domain.Currency;
import com.framework.subcriptions.dto.SubscriptionForm;
import com.framework.subcriptions.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService service;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("title", "구독 목록");
        model.addAttribute("subscriptions", service.findAll());
        return "subscriptions/index";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("title", "구독 등록");
        model.addAttribute("cycles", BillingCycle.values());
        model.addAttribute("currencies", Currency.values());
        return "subscriptions/new";
    }

    @PostMapping
    public String create(SubscriptionForm form) {
        service.create(form);
        return "redirect:/subscriptions";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("title", "구독 상세");
        model.addAttribute("subscription", service.findById(id));
        return "subscriptions/show";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("title", "구독 수정");
        model.addAttribute("subscription", service.findById(id));
        model.addAttribute("cycles", BillingCycle.values());
        model.addAttribute("currencies", Currency.values());
        return "subscriptions/edit";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, SubscriptionForm form) {
        service.update(id, form);
        return "redirect:/subscriptions/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/subscriptions";
    }
}
