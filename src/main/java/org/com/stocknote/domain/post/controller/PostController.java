package org.com.stocknote.domain.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.post.dto.PostCreateDto;
import org.com.stocknote.domain.post.dto.PostModifyDto;
import org.com.stocknote.domain.post.dto.PostResponseDto;
import org.com.stocknote.domain.post.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Long createPost(
            @Valid @RequestBody PostCreateDto postCreateDto,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return postService.createPost(postCreateDto, email);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(pageable));
    }

    @GetMapping("/{id}")
    public PostResponseDto getPostById(@PathVariable("id") Long id) {
        return postService.getPostById(id);
    }

    @PutMapping("/{id}")
    public void updatePost(
            @PathVariable("id") Long id,
            @Valid @RequestBody PostModifyDto postModifyDto
    ) {
        postService.updatePost(id, postModifyDto);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable("id") Long id) {
        // 게시글 삭제 권한 확인 필요 (작성자만 삭제 가능하다면)
        postService.deletePost(id);
    }
}