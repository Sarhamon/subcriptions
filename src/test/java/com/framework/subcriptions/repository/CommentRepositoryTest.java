package com.framework.subcriptions.repository;

import com.framework.subcriptions.domain.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// CommentRepository 조회 쿼리 검증. data.sql 시드(구독 1번에 댓글 2개, 4번에 1개) 위에서 확인.
@SpringBootTest
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Test
    void findBySubscriptionId_해당_구독의_댓글을_모두_반환한다() {
        List<Comment> comments = commentRepository.findBySubscriptionId(1L);

        assertEquals(2, comments.size());
        assertTrue(comments.stream().allMatch(c -> c.getSubscription().getId().equals(1L)));
    }

    @Test
    void findBySubscriptionId_댓글이_없는_구독은_빈_목록을_반환한다() {
        List<Comment> comments = commentRepository.findBySubscriptionId(2L);

        assertTrue(comments.isEmpty());
    }
}
