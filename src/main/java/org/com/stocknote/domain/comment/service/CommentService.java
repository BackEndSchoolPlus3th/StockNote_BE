package org.com.stocknote.domain.comment.service;

import lombok.RequiredArgsConstructor;

import org.com.stocknote.domain.comment.dto.CommentDetailResponse;
import org.com.stocknote.domain.comment.dto.CommentRequest;
import org.com.stocknote.domain.comment.dto.CommentUpdateDto;
import org.com.stocknote.domain.comment.dto.MyCommentResponse;
import org.com.stocknote.domain.comment.entity.Comment;
import org.com.stocknote.domain.comment.repository.CommentRepository;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;

import org.com.stocknote.domain.post.dto.MyPostResponseDto;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;


    @Transactional(readOnly = true)
    public CommentDetailResponse getCommentDetail(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        Member member = memberRepository.findById(comment.getMember().getId()).orElseThrow(() -> new IllegalArgumentException("user not found"));

        return new CommentDetailResponse(commentId, comment.getBody(), member.getName(), comment.getCreatedAt());
    }

    public Page<CommentDetailResponse> getComments(Long postId, Pageable pageable) {
        Page<CommentDetailResponse> comments = commentRepository.findByPostId(pageable, postId)
                .map(comment -> {
                            Member member = memberRepository.findById(comment.getMember().getId()).orElseThrow(() -> new IllegalArgumentException("user not found"));
                            return new CommentDetailResponse(comment.getId(), comment.getBody(), member.getName(), comment.getCreatedAt());
                        }
                );
        return comments;
    }

    @Transactional
    public Long createComment(Long postId, CommentRequest commentRequest, String userEmail) {
        Member member = memberRepository.findByEmail(userEmail).orElseThrow();
        Comment comment = new Comment(postId, commentRequest.getBody(), member);

        return commentRepository.save(comment).getId();
    }

    @Transactional
    public void updateComment(CommentUpdateDto commentUpdateDto) {
        Member member = memberRepository.findByEmail(commentUpdateDto.getUserEmail()).orElseThrow(() -> new IllegalArgumentException("user not found"));
        Comment comment = commentRepository.findById(commentUpdateDto.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!Objects.equals(member.getId(), comment.getMember().getId())) {
            throw new CustomException(ErrorCode.COMMENT_UPDATE_DENIED);
        }

        comment.setBody(commentUpdateDto.getBody());
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, String userEmail) {
        Member member = memberRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("user not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!Objects.equals(member.getId(), comment.getMember().getId())) {
            throw new CustomException(ErrorCode.COMMENT_DELETE_DENIED);
        }
        commentRepository.delete(comment);
    }

    public Page<MyCommentResponse> findCommentsByMember(Member member, Pageable pageable) {
        return commentRepository.findByMember(member, pageable).map(MyCommentResponse::of);
    }
}
