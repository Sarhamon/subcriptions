package com.framework.subcriptions.domain;

public class SubscriptionNotFoundException extends RuntimeException {

    public SubscriptionNotFoundException(Long id) {
        super("구독 정보를 찾을 수 없습니다. (id=" + id + ")");
    }
}
