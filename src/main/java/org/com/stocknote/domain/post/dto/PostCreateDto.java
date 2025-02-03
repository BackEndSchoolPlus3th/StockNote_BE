package org.com.stocknote.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.entity.PostCategory;

import java.util.List;

@Getter
@Setter
public class PostCreateDto {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;

    private List<String> hashtags;

    private PostCategory category;

    public Post toEntity(Member member) {
        return Post.builder()
                .member(member)
                .title(this.title)
                .body(this.body)
                .category(this.category)
                .build();
    }
}