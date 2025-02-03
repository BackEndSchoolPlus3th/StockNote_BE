package org.com.stocknote.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.like.entity.Like;
import org.com.stocknote.domain.like.repository.LikeRepository;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public void likePost(Long postId, Member member) {
        Post post = postRepository.findById(postId).orElseThrow();
        boolean alreadyLiked = likeRepository.findByMemberIdAndPostId(member.getId(), postId).isPresent();
        if (alreadyLiked) {
            throw new IllegalStateException("User has already liked this post.");
        }

        Like like = Like.builder()
                .member(member)
                .post(post)
                .build();

        likeRepository.save(like);
    }

    @Transactional
    public void unlikePost(Long postId, Member member) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Like like = likeRepository.findByMemberIdAndPostId(member.getId(), postId)
                .orElseThrow(() -> new IllegalStateException("User has not liked this post."));

        likeRepository.delete(like);
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public boolean hasUserLikedPost(Long postId, Member member) {
        return likeRepository.findByMemberIdAndPostId(member.getId(), postId).isPresent();
    }
}
