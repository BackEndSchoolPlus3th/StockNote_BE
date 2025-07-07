package org.com.stocknote.domain.searchDoc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.com.stocknote.domain.post.repository.PostRepository;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/public/test-auth")
@RequiredArgsConstructor
@Slf4j
public class TestAuthController {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @PostMapping("/create-test-user")
    public GlobalResponse<Map<String, Object>> createTestUser() {
        try {
            Optional<Member> existingMember = memberRepository.findByEmail("test@stocknote.com");

            Member testMember;
            if (existingMember.isPresent()) {
                testMember = existingMember.get();
                log.info("기존 테스트 사용자 사용: {}", testMember.getEmail());
            } else {
                testMember = Member.builder()
                        .email("test@stocknote.com")
                        .name("테스트사용자")
                        .profile("test-profile.jpg")
                        .build();

                testMember = memberRepository.save(testMember);
                log.info("새 테스트 사용자 생성: {}", testMember.getEmail());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("memberId", testMember.getId());
            response.put("email", testMember.getEmail());
            response.put("name", testMember.getName());
            response.put("message", "테스트 사용자 준비 완료");

            return GlobalResponse.success(response);

        } catch (Exception e) {
            log.error("테스트 사용자 생성 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return GlobalResponse.success(errorResponse);
        }
    }

    @PostMapping("/create-test-posts")
    @Transactional
    public GlobalResponse<String> createTestPosts(
            @RequestParam(defaultValue = "100") int postCount) {

        try {
            Member testMember = memberRepository.findByEmail("test@stocknote.com")
                    .orElseThrow(() -> new RuntimeException("테스트 사용자를 먼저 생성해주세요"));

            log.info("테스트 게시글 {}개 생성 시작", postCount);

            List<Post> posts = new ArrayList<>();
            String[] stockNames = {"삼성전자", "SK하이닉스", "LG에너지솔루션", "네이버", "카카오"};
            String[] keywords = {"매수", "매도", "분석", "전망", "추천"};
            PostCategory[] categories = {PostCategory.FREE, PostCategory.QNA, PostCategory.TIP};

            for (int i = 1; i <= postCount; i++) {
                String stockName = stockNames[i % stockNames.length];
                String keyword = keywords[i % keywords.length];
                PostCategory category = categories[i % categories.length];

                Post post = Post.builder()
                        .title(stockName + " " + keyword + " 게시글 " + i)
                        .body(stockName + "에 대한 " + keyword + " 내용입니다. 테스트용 게시글입니다.")
                        .category(category)
                        .member(testMember)
                        .comments(new ArrayList<>())
                        .likes(new ArrayList<>())
                        .build();

                posts.add(post);

                if (i % 100 == 0 || i == postCount) {
                    postRepository.saveAll(posts);
                    posts.clear();
                    log.info("게시글 {}개 저장 완료", i);
                }
            }

            return GlobalResponse.success(
                    String.format("테스트 게시글 %d개 생성 완료", postCount)
            );

        } catch (Exception e) {
            log.error("테스트 게시글 생성 실패", e);
            return GlobalResponse.success("테스트 게시글 생성 실패: " + e.getMessage());
        }
    }

    @GetMapping("/post-count")
    public GlobalResponse<Map<String, Object>> getPostCount() {
        try {
            long totalPosts = postRepository.count();

            Map<String, Object> response = new HashMap<>();
            response.put("totalPosts", totalPosts);
            response.put("message", "게시글 수 조회 완료");

            return GlobalResponse.success(response);

        } catch (Exception e) {
            log.error("게시글 수 조회 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return GlobalResponse.success(errorResponse);
        }
    }

    @GetMapping("/simple-posts")
    public GlobalResponse<Map<String, Object>> getSimplePosts(
            @RequestParam(defaultValue = "5") int size) {
        try {
            List<Post> posts = postRepository.findAll().stream()
                    .limit(size)
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("totalPosts", postRepository.count());
            response.put("foundPosts", posts.size());
            response.put("posts", posts.stream().map(post -> {
                Map<String, Object> postInfo = new HashMap<>();
                postInfo.put("id", post.getId());
                postInfo.put("title", post.getTitle());
                postInfo.put("category", post.getCategory());
                postInfo.put("deletedAt", post.getDeletedAt());
                return postInfo;
            }).toList());

            return GlobalResponse.success(response);

        } catch (Exception e) {
            log.error("간단한 게시글 조회 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return GlobalResponse.success(errorResponse);
        }
    }
}