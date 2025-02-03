package org.com.stocknote.domain.comment.dto;

import org.com.stocknote.domain.comment.entity.Comment;

import java.time.LocalDateTime;

public record MyCommentResponse(
        Long id,
        String body,
        Long postId,
        LocalDateTime createdAt
) {
    // Post 엔티티를 DTO로 변환하는 팩토리 메서드
    public static MyCommentResponse of(Comment comment) {
        return new MyCommentResponse(
                comment.getId(),
                comment.getBody(),
                comment.getPostId(),
                comment.getCreatedAt()
        );
    }
}
