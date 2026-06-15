package com.framework.subcriptions.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 구독 1건에 달리는 댓글(메모). 구독 → 댓글 단방향 @ManyToOne만 둔다.
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Comment {

    // DB가 채우는 PK (H2 IDENTITY → auto increment).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 구독에 달린 댓글인지. 단방향이라 Subscription 쪽은 이 관계를 알지 못한다.
    @ManyToOne
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    // 작성자 닉네임.
    @Column(nullable = false)
    private String nickname;

    // 댓글 본문.
    @Column(nullable = false)
    private String body;
}
