package org.com.stocknote.domain.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.post.dto.MyPostResponseDto;
import org.com.stocknote.domain.post.dto.PostCreateDto;
import org.com.stocknote.domain.post.dto.PostModifyDto;
import org.com.stocknote.domain.post.dto.PostResponseDto;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.com.stocknote.domain.post.service.PostService;
import org.com.stocknote.global.dto.GlobalResponse;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "커뮤니티 게시글 API", description = "게시글(Post)")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "게시글 작성")
    public GlobalResponse<Long> createPost(
            @Valid @RequestBody PostCreateDto postCreateDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Member member = principalDetails.user();
        return GlobalResponse.success(postService.createPost(postCreateDto, member));
    }

//    @GetMapping
//    public GlobalResponse<Page<PostResponseDto>> getAllPosts(Pageable pageable) {
//        return GlobalResponse.success(postService.getPosts(pageable));
//    }

    @GetMapping
    @Operation(summary = "게시글 목록 조회")
    public GlobalResponse<Page<PostResponseDto>> getPosts(
            @RequestParam(required = false, name = "category") PostCategory category,
            Pageable pageable
    ) {
        if (category != null) {
            return GlobalResponse.success(postService.getPostsByCategory(category, pageable));
        }
        return GlobalResponse.success(postService.getPosts(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회")
    public GlobalResponse<PostResponseDto> getPostById(@PathVariable("id") Long id) {
        return GlobalResponse.success(postService.getPostById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정")
    public GlobalResponse<String> updatePost(
            @PathVariable("id") Long id,
            @Valid @RequestBody PostModifyDto postModifyDto
    ) {
        postService.updatePost(id, postModifyDto);
        return GlobalResponse.success("Post updated successfully");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제")
    public GlobalResponse<String> deletePost(@PathVariable("id") Long id) {
        postService.deletePost(id);
        return GlobalResponse.success("Post deleted successfully");
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "내가 작성한 게시글 목록 조회")
    public GlobalResponse<Page<MyPostResponseDto>> getMyPosts(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            Pageable pageable
    ) {
        Member member = principalDetails.user();
        return GlobalResponse.success(
                postService.findPostsByMember(member, pageable));
    }
}