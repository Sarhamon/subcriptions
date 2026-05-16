package com.framework.subcriptions.controller;

import com.framework.subcriptions.domain.SubscriptionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SubscriptionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(SubscriptionNotFoundException e, Model model) {
        model.addAttribute("title", "찾을 수 없음");
        model.addAttribute("message", e.getMessage());
        return "error/404";
    }
}
