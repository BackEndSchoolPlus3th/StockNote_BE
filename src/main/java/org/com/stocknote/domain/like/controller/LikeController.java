package org.com.stocknote.domain.like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.like.service.LikeService;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.global.dto.GlobalResponse;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "게시글 좋아요 API", description = "좋아요(Like)")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{postId}/like")
    @Operation(summary = "게시글 좋아요")
    public GlobalResponse<String> likePost(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Member member = principalDetails.user();
        likeService.likePost(postId, member);
        return GlobalResponse.success("Post liked successfully.");

    }

    @PostMapping("/{postId}/unlike")
    @Operation(summary = "게시글 좋아요 취소")
    public GlobalResponse<String> unlikePost(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Member member = principalDetails.user();
        likeService.unlikePost(postId, member);
        return GlobalResponse.success("Post unliked successfully.");
    }

    @GetMapping("/{postId}/likes/check")
    @Operation(summary = "게시글 좋아요 상태 확인")
    public GlobalResponse<Boolean> hasUserLikedPost(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Member member = principalDetails.user();
        boolean hasLiked = likeService.hasUserLikedPost(postId, member);
        return GlobalResponse.success(hasLiked);
    }
}
