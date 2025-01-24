package org.com.stocknote.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.domain.post.dto.PostCreateDto;
import org.com.stocknote.domain.post.dto.PostModifyDto;
import org.com.stocknote.domain.post.dto.PostResponseDto;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long createPost(PostCreateDto postCreateDto, String userEmail) {
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Member not found for email = " + userEmail));
        Post post = postCreateDto.toEntity(member.getId());
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(PostResponseDto::fromPost);
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return PostResponseDto.fromPost(post);
    }

    @Transactional
    public void updatePost(Long id, PostModifyDto postModifyDto) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        existingPost.bodyUpdate(postModifyDto.getBody());
        existingPost.titleUpdate(postModifyDto.getTitle());
        postRepository.save(existingPost);
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.softDelete();
        postRepository.save(post);
    }
}