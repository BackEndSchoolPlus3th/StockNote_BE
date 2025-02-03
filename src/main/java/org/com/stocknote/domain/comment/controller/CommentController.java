package org.com.stocknote.domain.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.comment.dto.CommentDetailResponse;
import org.com.stocknote.domain.comment.dto.CommentRequest;
import org.com.stocknote.domain.comment.dto.CommentUpdateDto;
import org.com.stocknote.domain.comment.dto.MyCommentResponse;
import org.com.stocknote.domain.comment.service.CommentService;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.global.dto.GlobalResponse;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
@Tag(name = "게시판 댓글 API", description = "댓글(comment)")
public class CommentController {

    private final CommentService commentService;


    @GetMapping
    @Operation(summary = "댓글 목록 조회")
    public GlobalResponse<Page<CommentDetailResponse>> getComments(@PathVariable(value = "postId") Long postId, Pageable pageable) {
        return GlobalResponse.success(commentService.getComments(postId, pageable));
    }

    @GetMapping("/{commentId}")
    @Operation(summary = "댓글 상세 조회")
    public GlobalResponse<CommentDetailResponse> getComment(@PathVariable(value = "commentId") Long commentId) {
        return GlobalResponse.success(commentService.getCommentDetail(commentId));
    }

    @PostMapping
    @Operation(summary = "댓글 작성")
    public GlobalResponse<Long> createComment(@PathVariable(value = "postId") Long postId,
                                              @RequestBody CommentRequest commentRequest,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {

        Member member = principalDetails.user();
        return GlobalResponse.success(commentService.createComment(postId, commentRequest, member.getEmail()));
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "댓글 수정")
    public GlobalResponse<Void> updateComment(@PathVariable(value = "postId") Long postId, @PathVariable(value = "commentId") Long commentId, @RequestBody CommentRequest commentRequest, Authentication authentication) {
        String userEmail = authentication.getPrincipal().toString();

        CommentUpdateDto commentUpdateDto = new CommentUpdateDto(postId, commentId, commentRequest.getBody(), userEmail);

        commentService.updateComment(commentUpdateDto);

        return GlobalResponse.success();
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제")
    public GlobalResponse<Void> deleteComment(@PathVariable(value = "commentId") Long commentId, Authentication authentication) {
        String userEmail = authentication.getPrincipal().toString();
        commentService.deleteComment(commentId, userEmail);

        return GlobalResponse.success();
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "내가 작성한 댓글 목록 조회")
    public GlobalResponse<Page<MyCommentResponse>> getMyComments(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            Pageable pageable
    ) {
        Member member = principalDetails.user();
        return GlobalResponse.success(
                commentService.findCommentsByMember(member, pageable)
        );
    }
}