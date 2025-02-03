package org.com.stocknote.domain.post.dto;

import org.com.stocknote.domain.post.entity.Post;

import java.time.LocalDateTime;

public record MyPostResponseDto(
        Long id,
        String title,
        String body,
        LocalDateTime createdAt
) {

    public static MyPostResponseDto of(Post post) {
        return new MyPostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                post.getCreatedAt()
        );
    }
}

