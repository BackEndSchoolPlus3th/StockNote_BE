package org.com.stocknote.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.com.stocknote.domain.post.entity.Post;

@Getter
@Setter
public class PostCreateDto {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;


    public Post toEntity(Long userId) {
        return Post.builder()
                .userId(userId)
                .title(this.title)
                .body(this.body)
                .build();
    }
}