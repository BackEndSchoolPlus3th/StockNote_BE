package org.com.stocknote.domain.post.dto;

import org.com.stocknote.domain.post.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponseDto(
        Long id,
        String title,
        String body,
        String username,
        String profile,
        LocalDateTime createdAt,
        List<String>hashtags,
        int likeCount
) {
    public static PostResponseDto fromPost(Post post, List<String> hashtags) {
        return new PostResponseDto(post.getId(), post.getTitle(), post.getBody(), post.getMember().getName(), post.getMember().getProfile(), post.getCreatedAt(), hashtags, post.getLikeList().size());
    }
}

