package com.framework.subcriptions.repository;

import com.framework.subcriptions.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

// Subscription 영속화 인터페이스. JpaRepository가 CRUD 메서드를 자동 제공한다.
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
