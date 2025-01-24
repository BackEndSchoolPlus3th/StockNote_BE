package org.com.stocknote.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.com.stocknote.domain.post.entity.Post;

import java.util.List;

@Getter
@Setter
public class PostModifyDto {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;

    private List<String> hashtags;


    public Post toEntity(Long id, Long userId) {
        return Post.builder()
                .id(id)
                .title(this.title)
                .body(this.body)
                .userId(userId)
                .build();
    }
}