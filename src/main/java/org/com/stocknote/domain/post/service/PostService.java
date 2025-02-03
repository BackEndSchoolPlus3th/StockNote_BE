package org.com.stocknote.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.comment.repository.CommentRepository;
import org.com.stocknote.domain.hashtag.entity.Hashtag;
import org.com.stocknote.domain.hashtag.service.HashtagService;
import org.com.stocknote.domain.like.repository.LikeRepository;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.post.dto.MyPostResponseDto;
import org.com.stocknote.domain.post.dto.PostCreateDto;
import org.com.stocknote.domain.post.dto.PostModifyDto;
import org.com.stocknote.domain.post.dto.PostResponseDto;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.com.stocknote.domain.post.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final HashtagService hashtagService;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public Long createPost(PostCreateDto postCreateDto, Member member) {

        Post post = postCreateDto.toEntity(member);
        Post savedPost = postRepository.save(post);

        hashtagService.createHashtags(savedPost.getId(), postCreateDto.getHashtags());
        return savedPost.getId();
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(post -> {
            List<String> hashtags = hashtagService.getHashtagsByPostId(post.getId()).stream()
                    .map(Hashtag::getName).toList();

//            Long likeCount = likeRepository.countByPostId(post.getId());

            return PostResponseDto.fromPost(post, hashtags);
        });
    }

    @Transactional(readOnly=true)
    public Page<PostResponseDto> getPostsByCategory(PostCategory category, Pageable pageable) {
        Page<Post> posts = postRepository.findByCategory(category, pageable);  // Pageable 추가
        return posts.map(post -> {
            List<String> hashtags = hashtagService.getHashtagsByPostId(post.getId())
                    .stream()
                    .map(Hashtag::getName)
                    .toList();

//            Long likeCount = likeRepository.countByPostId(post.getId());
            return PostResponseDto.fromPost(post, hashtags);

        });
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        List<String> hashtags = hashtagService.getHashtagsByPostId(post.getId())
                .stream()
                .map(Hashtag::getName)
                .toList();

//        Long likeCount = likeRepository.countByPostId(post.getId());
        return PostResponseDto.fromPost(post, hashtags);
    }

    @Transactional
    public void updatePost(Long id, PostModifyDto postModifyDto) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        existingPost.bodyUpdate(postModifyDto.getBody());
        existingPost.titleUpdate(postModifyDto.getTitle());
        postRepository.save(existingPost);

        hashtagService.updateHashtags(id, postModifyDto.getHashtags());
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.softDelete();
        postRepository.save(post);
        commentRepository.deleteByPostId(postId);
        likeRepository.deleteByPostId(postId);
        hashtagService.deleteHashtagsByPostId(postId);
    }

    public Page<MyPostResponseDto> findPostsByMember(Member member, Pageable pageable) {
        return postRepository.findByMember(member, pageable).map(MyPostResponseDto::of);
    }
}
