package com.framework.subcriptions.domain;

// 존재하지 않는 구독 ID를 조회/수정/삭제할 때 던지는 도메인 예외.
public class SubscriptionNotFoundException extends RuntimeException {

    // 사용자에게 보여줄 한국어 메시지에 식별자를 포함시킨다.
    public SubscriptionNotFoundException(Long id) {
        super("구독 정보를 찾을 수 없습니다. (id=" + id + ")");
    }
}
