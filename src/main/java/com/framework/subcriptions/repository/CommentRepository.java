package com.framework.subcriptions.repository;

import com.framework.subcriptions.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Comment 영속화 인터페이스. JpaRepository의 기본 CRUD에 더해 구독별 조회 메서드를 제공한다.
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 구독에 달린 댓글을 모두 조회 (메서드 이름 기반 쿼리).
    List<Comment> findBySubscriptionId(Long subscriptionId);
}
