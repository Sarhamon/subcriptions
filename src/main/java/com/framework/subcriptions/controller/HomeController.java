package com.framework.subcriptions.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 루트(/) 접속을 구독 목록으로 보내는 진입점 컨트롤러.
@Controller
public class HomeController {

    // "/"로 들어오면 구독 목록 페이지로 302 리다이렉트한다.
    @GetMapping("/")
    public String home() {
        return "redirect:/subscriptions";
    }
}
