package com.framework.subcriptions.controller;

import com.framework.subcriptions.domain.Subscription;
import com.framework.subcriptions.domain.SubscriptionNotFoundException;
import com.framework.subcriptions.dto.SubscriptionForm;
import com.framework.subcriptions.dto.SubscriptionView;
import com.framework.subcriptions.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

// 구독 데이터를 JSON으로 제공하는 REST API. @RestController라 반환값이 곧 응답 본문으로 직렬화된다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionApiController {

    private final SubscriptionService service;

    // GET /api/subscriptions — 전체 구독 목록을 JSON 배열로 반환.
    @GetMapping
    public List<SubscriptionView> index() {
        return service.findAll();
    }

    // GET /api/subscriptions/{id} — 단건 구독을 JSON으로 반환. 없으면 아래 핸들러가 404 JSON 처리.
    @GetMapping("/{id}")
    public SubscriptionView show(@PathVariable Long id) {
        return service.findById(id);
    }

    // POST /api/subscriptions — JSON 본문으로 신규 구독 생성. 201 + 생성된 구독 JSON 반환.
    @PostMapping
    public ResponseEntity<SubscriptionView> create(@RequestBody SubscriptionForm form) {
        Subscription created = service.create(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.findById(created.getId()));
    }

    // PATCH /api/subscriptions/{id} — JSON 본문으로 수정. 없으면 아래 핸들러가 404 처리.
    @PatchMapping("/{id}")
    public SubscriptionView update(@PathVariable Long id, @RequestBody SubscriptionForm form) {
        service.update(id, form);
        return service.findById(id);
    }

    // DELETE /api/subscriptions/{id} — 단건 삭제. 본문 없이 204 No Content. 없으면 404.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // REST 응답용 404 처리. 컨트롤러-로컬 핸들러가 GlobalExceptionHandler(HTML 뷰)보다 우선한다.
    @ExceptionHandler(SubscriptionNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(SubscriptionNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
    }
}
