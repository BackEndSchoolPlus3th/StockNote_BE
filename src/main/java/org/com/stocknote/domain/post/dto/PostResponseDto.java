package org.com.stocknote.domain.post.dto;

import org.com.stocknote.domain.post.entity.Post;

import java.time.LocalDateTime;

public record PostResponseDto(
        Long id,
        String title,
        String body,
        Long userId,
        LocalDateTime createdAt
//        List<String>hashtag
) {
    public static PostResponseDto fromPost(Post post) {
        return new PostResponseDto(post.getId(), post.getTitle(), post.getBody(), post.getUserId(), post.getCreatedAt());
    }
}

