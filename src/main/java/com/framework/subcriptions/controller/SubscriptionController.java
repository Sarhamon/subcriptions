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

// 구독 CRUD HTTP 진입점. 모든 요청을 SubscriptionService에 위임하고 뷰 이름만 결정.
@Controller
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {

    // Lombok @RequiredArgsConstructor가 final 필드 주입용 생성자를 자동 생성.
    private final SubscriptionService service;

    // GET /subscriptions — 전체 구독 카드 목록 페이지.
    @GetMapping
    public String index(Model model) {
        model.addAttribute("title", "전체 구독");
        model.addAttribute("subscriptions", service.findAll());
        return "subscriptions/index";
    }

    // GET /subscriptions/new — 등록 폼. enum 선택지를 함께 내려준다.
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("title", "구독 등록");
        model.addAttribute("cycles", BillingCycle.values());
        model.addAttribute("currencies", Currency.values());
        return "subscriptions/new";
    }

    // POST /subscriptions — 신규 구독 저장 후 목록으로 PRG 리다이렉트.
    @PostMapping
    public String create(SubscriptionForm form) {
        service.create(form);
        return "redirect:/subscriptions";
    }

    // GET /subscriptions/{id} — 단건 상세 페이지.
    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("title", "구독 상세");
        model.addAttribute("subscription", service.findById(id));
        return "subscriptions/show";
    }

    // GET /subscriptions/{id}/edit — 수정 폼. 기존 값 + enum 선택지를 함께 내려준다.
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("title", "구독 수정");
        model.addAttribute("subscription", service.findById(id));
        model.addAttribute("cycles", BillingCycle.values());
        model.addAttribute("currencies", Currency.values());
        return "subscriptions/edit";
    }

    // POST /subscriptions/{id}/update — 수정 반영 후 상세 페이지로 PRG.
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, SubscriptionForm form) {
        service.update(id, form);
        return "redirect:/subscriptions/" + id;
    }

    // POST /subscriptions/{id}/delete — 단건 삭제 후 목록으로 PRG.
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/subscriptions";
    }
}
