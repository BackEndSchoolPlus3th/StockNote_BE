package org.com.stocknote.domain.searchDoc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.comment.entity.Comment;
import org.com.stocknote.domain.comment.repository.CommentRepository;
import org.com.stocknote.domain.hashtag.entity.Hashtag;
import org.com.stocknote.domain.hashtag.repository.HashtagRepository;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.domain.post.dto.PostSearchConditionDto;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.com.stocknote.domain.post.repository.PostRepository;
import org.com.stocknote.domain.searchDoc.document.PostDoc;
import org.com.stocknote.domain.searchDoc.dto.PerformanceTestResult;
import org.com.stocknote.domain.searchDoc.dto.SystemStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceTestService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final HashtagRepository hashtagRepository;
    private final SearchDocService searchDocService;
    private final ElasticsearchOperations elasticsearchOperations;

    private final Random random = new Random();
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    // 개선된 더미 데이터 생성 - 10만 게시글, 50만 댓글
    @Transactional
    public void generateLargeDummyData(int postCount, int commentCount) {
        log.info("🚀 대용량 더미 데이터 생성 시작 - 게시글: {}개, 댓글: {}개", postCount, commentCount);

        // 1. 테스트용 회원 생성 (100명)
        List<Member> testMembers = createTestMembers(100);

        // 2. 게시글 배치 생성 (10만개)
        List<Post> allPosts = generatePostsInBatches(postCount, testMembers);

        // 3. 해시태그 생성
        generateHashtagsForPosts(allPosts);

        // 4. 댓글 배치 생성 (50만개)
        generateCommentsInBatches(commentCount, allPosts, testMembers);

        // 5. ElasticSearch 동기화
        syncAllToElasticSearch(allPosts);

        log.info("✅ 대용량 더미 데이터 생성 완료!");
    }

    private List<Member> createTestMembers(int memberCount) {
        log.info("📝 테스트 회원 {}명 생성 중...", memberCount);
        List<Member> members = new ArrayList<>();

        for (int i = 1; i <= memberCount; i++) {
            Member member = Member.builder()
                    .email("test" + i + "@stocknote.com")
                    .name("테스트사용자" + i)
                    .profile("profile" + i + ".jpg")
                    .build();
            members.add(member);
        }

        return memberRepository.saveAll(members);
    }

    private List<Post> generatePostsInBatches(int totalPosts, List<Member> members) {
        log.info("📄 게시글 {}개 배치 생성 중...", totalPosts);

        String[] stockNames = {
                "삼성전자", "SK하이닉스", "LG에너지솔루션", "네이버", "카카오", "현대차",
                "POSCO", "셀트리온", "한국전력", "신한지주", "KB금융", "LG화학",
                "기아", "NAVER", "삼성바이오로직스", "현대모비스", "LG생활건강", "한국가스공사"
        };

        String[] investmentTerms = {
                "매수", "매도", "분석", "전망", "수익", "손실", "차트", "기술적분석",
                "재무분석", "투자", "배당", "주가", "상승", "하락", "추천", "목표가"
        };

        PostCategory[] categories = {PostCategory.FREE, PostCategory.QNA, PostCategory.TIP, PostCategory.NEWS};

        List<Post> allPosts = new ArrayList<>();
        int batchSize = 1000;

        for (int batch = 0; batch < totalPosts / batchSize; batch++) {
            List<Post> batchPosts = new ArrayList<>();

            for (int i = 0; i < batchSize; i++) {
                Member randomMember = members.get(random.nextInt(members.size()));
                String stockName = stockNames[random.nextInt(stockNames.length)];
                String investmentTerm = investmentTerms[random.nextInt(investmentTerms.length)];
                PostCategory category = categories[random.nextInt(categories.length)];

                Post post = Post.builder()
                        .title(stockName + " " + investmentTerm + " 관련 게시글 " + (batch * batchSize + i + 1))
                        .body(generateDetailedPostContent(stockName, investmentTerm))
                        .category(category)
                        .member(randomMember)
                        .comments(new ArrayList<>())
                        .likes(new ArrayList<>())
                        .build();

                batchPosts.add(post);
            }

            List<Post> savedPosts = postRepository.saveAll(batchPosts);
            allPosts.addAll(savedPosts);

            if ((batch + 1) % 10 == 0) {
                log.info("📄 게시글 {}개 생성 완료", (batch + 1) * batchSize);
            }
        }

        return allPosts;
    }

    private String generateDetailedPostContent(String stockName, String investmentTerm) {
        String[] templates = {
                "%s %s에 대한 상세 분석입니다. 최근 실적과 기술적 지표를 종합해보면 긍정적인 신호가 보입니다.",
                "%s %s 관련하여 전문가들의 의견을 정리해보았습니다. 펀더멘털 분석 결과 매력적인 투자처로 판단됩니다.",
                "%s %s 전략에 대해 공유드립니다. 차트 패턴과 거래량 분석을 통해 향후 방향성을 예측해보겠습니다.",
                "%s %s 정보를 업데이트합니다. 최신 뉴스와 공시 내용을 바탕으로 투자 의견을 제시합니다."
        };

        String template = templates[random.nextInt(templates.length)];
        return String.format(template, stockName, investmentTerm) +
                " 투자에는 항상 리스크가 따르므로 신중한 판단이 필요합니다. " +
                "개인의 투자 성향과 자금 상황을 고려하여 결정하시기 바랍니다.";
    }

    private void generateHashtagsForPosts(List<Post> posts) {
        log.info("🏷️ 해시태그 생성 중...");

        String[] hashtags = {
                "주식투자", "재테크", "배당주", "성장주", "가치투자", "기술적분석",
                "펀더멘털", "코스피", "코스닥", "ETF", "리츠", "IPO"
        };

        List<Hashtag> allHashtags = new ArrayList<>();

        for (Post post : posts) {
            int hashtagCount = random.nextInt(3) + 1; // 1-3개 해시태그

            for (int i = 0; i < hashtagCount; i++) {
                String hashtagName = hashtags[random.nextInt(hashtags.length)];
                Hashtag hashtag = Hashtag.create(hashtagName, post.getId());
                allHashtags.add(hashtag);
            }
        }

        hashtagRepository.saveAll(allHashtags);
        log.info("🏷️ 해시태그 {}개 생성 완료", allHashtags.size());
    }

    private void generateCommentsInBatches(int totalComments, List<Post> posts, List<Member> members) {
        log.info("💬 댓글 {}개 배치 생성 중...", totalComments);

        String[] commentTexts = {
                "좋은 정보 감사합니다!", "동의합니다. 추가 분석 부탁드려요.",
                "반대 의견입니다. 근거가 부족해 보여요.", "더 자세한 차트 분석이 필요할 것 같아요.",
                "실제로 투자해봤는데 맞는 것 같아요.", "리스크 관리도 중요하겠네요.",
                "좋은 관점이네요. 참고하겠습니다.", "시장 상황을 잘 분석하신 것 같아요.",
                "비슷한 생각이었는데 확신이 서네요.", "다른 종목은 어떻게 보시나요?"
        };

        int batchSize = 5000;

        for (int batch = 0; batch < totalComments / batchSize; batch++) {
            List<Comment> batchComments = new ArrayList<>();

            for (int i = 0; i < batchSize; i++) {
                Post randomPost = posts.get(random.nextInt(posts.size()));
                Member randomMember = members.get(random.nextInt(members.size()));
                String commentText = commentTexts[random.nextInt(commentTexts.length)];

                Comment comment = new Comment(randomPost, commentText, randomMember);
                batchComments.add(comment);
            }

            commentRepository.saveAll(batchComments);

            if ((batch + 1) % 10 == 0) {
                log.info("💬 댓글 {}개 생성 완료", (batch + 1) * batchSize);
            }
        }
    }

    private void syncAllToElasticSearch(List<Post> posts) {
        log.info("🔄 ElasticSearch 동기화 시작...");

        try {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            int batchSize = 500;

            for (int i = 0; i < posts.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, posts.size());
                List<Post> batch = posts.subList(i, endIndex);

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    List<PostDoc> postDocs = new ArrayList<>();

                    for (Post post : batch) {
                        try {
                            PostDoc postDoc = searchDocService.transformPostDoc(post);
                            postDocs.add(postDoc);
                        } catch (Exception e) {
                            log.warn("Post ID {} ElasticSearch 동기화 실패: {}", post.getId(), e.getMessage());
                        }
                    }

                    if (!postDocs.isEmpty()) {
                        elasticsearchOperations.save(postDocs);
                    }
                }, executor);

                futures.add(future);
            }

            // 모든 배치 완료 대기
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            log.info("✅ ElasticSearch 동기화 완료");

        } catch (Exception e) {
            log.error("❌ ElasticSearch 동기화 실패", e);
        }
    }

    // MySQL 검색 성능 테스트 (기존 코드 개선)
    public PerformanceTestResult testMySQL(String keyword, Pageable pageable, int testCount) {
        List<Long> responseTimes = new ArrayList<>();
        long totalCount = 0;

        // 워밍업
        postRepository.findAll(pageable);

        log.info("🔍 MySQL 검색 테스트 시작 - 키워드: {}, 테스트 횟수: {}", keyword, testCount);

        for (int i = 0; i < testCount; i++) {
            long startTime = System.nanoTime();

            Page<Post> results = postRepository.findByTitleContainingOrBodyContaining(keyword, keyword, pageable);

            long endTime = System.nanoTime();
            long responseTime = (endTime - startTime) / 1_000_000; // ms 변환

            responseTimes.add(responseTime);
            totalCount = results.getTotalElements();

            if (i % 100 == 0) {
                log.debug("MySQL 테스트 진행률: {}/{}", i + 1, testCount);
            }
        }

        double averageResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double throughput = testCount / (averageResponseTime / 1000.0);

        return PerformanceTestResult.builder()
                .searchEngine("MySQL")
                .averageResponseTime(Math.round(averageResponseTime * 100.0) / 100.0)
                .minResponseTime(responseTimes.stream().mapToLong(Long::longValue).min().orElse(0))
                .maxResponseTime(responseTimes.stream().mapToLong(Long::longValue).max().orElse(0))
                .throughput(Math.round(throughput * 100.0) / 100.0)
                .totalResults(totalCount)
                .testCount(testCount)
                .build();
    }

    // ElasticSearch 검색 성능 테스트 (기존 코드 개선)
    public PerformanceTestResult testElasticSearch(PostSearchConditionDto condition, Pageable pageable, int testCount) {
        List<Long> responseTimes = new ArrayList<>();
        long totalCount = 0;

        // 워밍업
        searchDocService.searchPosts(condition, pageable);

        log.info("🔍 ElasticSearch 검색 테스트 시작 - 키워드: {}, 테스트 횟수: {}", condition.getKeyword(), testCount);

        for (int i = 0; i < testCount; i++) {
            long startTime = System.nanoTime();

            Page<PostDoc> results = searchDocService.searchPosts(condition, pageable);

            long endTime = System.nanoTime();
            long responseTime = (endTime - startTime) / 1_000_000; // ms 변환

            responseTimes.add(responseTime);
            totalCount = results.getTotalElements();

            if (i % 100 == 0) {
                log.debug("ElasticSearch 테스트 진행률: {}/{}", i + 1, testCount);
            }
        }

        double averageResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double throughput = testCount / (averageResponseTime / 1000.0);

        return PerformanceTestResult.builder()
                .searchEngine("ElasticSearch")
                .averageResponseTime(Math.round(averageResponseTime * 100.0) / 100.0)
                .minResponseTime(responseTimes.stream().mapToLong(Long::longValue).min().orElse(0))
                .maxResponseTime(responseTimes.stream().mapToLong(Long::longValue).max().orElse(0))
                .throughput(Math.round(throughput * 100.0) / 100.0)
                .totalResults(totalCount)
                .testCount(testCount)
                .build();
    }

    public SystemStats getSystemStats() {
        long postCount = postRepository.count();
        long commentCount = commentRepository.count();
        long memberCount = memberRepository.count();

        return SystemStats.builder()
                .totalPosts(postCount)
                .totalComments(commentCount)
                .totalMembers(memberCount)
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    // 리소스 정리
    public void cleanup() {
        executor.shutdown();
    }
}