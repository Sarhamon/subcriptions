package com.framework.subcriptions.controller;

import com.framework.subcriptions.domain.SubscriptionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

// 모든 컨트롤러에서 발생한 도메인 예외를 잡아 공통 에러 뷰로 변환한다.
@ControllerAdvice
public class GlobalExceptionHandler {

    // 구독 미발견 예외를 404 응답 + error/404 뷰로 매핑.
    @ExceptionHandler(SubscriptionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(SubscriptionNotFoundException e, Model model) {
        model.addAttribute("title", "찾을 수 없음");
        model.addAttribute("message", e.getMessage());
        return "error/404";
    }
}
