package org.com.stocknote.domain.searchDoc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.post.dto.PostSearchConditionDto;
import org.com.stocknote.domain.post.repository.PostRepository;
import org.com.stocknote.domain.searchDoc.dto.PerformanceCompareResult;
import org.com.stocknote.domain.searchDoc.dto.PerformanceGain;
import org.com.stocknote.domain.searchDoc.dto.PerformanceTestResult;
import org.com.stocknote.domain.searchDoc.dto.SystemStats;
import org.com.stocknote.domain.searchDoc.dto.LoadTestResult;
import org.com.stocknote.domain.searchDoc.dto.SystemResourceInfo;
import org.com.stocknote.domain.searchDoc.dto.ComprehensiveTestResult;
import org.com.stocknote.domain.searchDoc.service.PerformanceTestService;
import org.com.stocknote.domain.searchDoc.service.LoadTestService;
import org.com.stocknote.domain.searchDoc.service.SearchDocService;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/performance")
@RequiredArgsConstructor
@Tag(name = "성능 비교 API", description = "MySQL vs ElasticSearch 성능 분석 및 부하 테스트")
@Slf4j
public class PerformanceController {

    private final PostRepository postRepository;
    private final SearchDocService searchDocService;
    private final PerformanceTestService performanceTestService;
    private final LoadTestService loadTestService;

    @GetMapping("/compare-search")
    @Operation(summary = "MySQL vs ElasticSearch 검색 성능 비교")
    public GlobalResponse<PerformanceCompareResult> compareSearchPerformance(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "100") int testCount) {

        log.info("🔍 검색 성능 비교 시작 - 키워드: {}, 테스트 횟수: {}", keyword, testCount);

        Pageable pageable = PageRequest.of(0, size);
        PostSearchConditionDto condition = new PostSearchConditionDto();
        condition.setKeyword(keyword);
        condition.setSearchType(PostSearchConditionDto.SearchType.ALL);

        // 병렬 테스트 실행
        CompletableFuture<PerformanceTestResult> mysqlTest = CompletableFuture.supplyAsync(() ->
                performanceTestService.testMySQL(keyword, pageable, testCount));

        CompletableFuture<PerformanceTestResult> elasticTest = CompletableFuture.supplyAsync(() ->
                performanceTestService.testElasticSearch(condition, pageable, testCount));

        // 결과 대기
        PerformanceTestResult mysqlResult = mysqlTest.join();
        PerformanceTestResult elasticResult = elasticTest.join();

        PerformanceCompareResult result = PerformanceCompareResult.builder()
                .keyword(keyword)
                .testCount(testCount)
                .mysqlResult(mysqlResult)
                .elasticSearchResult(elasticResult)
                .performanceGain(calculatePerformanceGain(mysqlResult, elasticResult))
                .build();

        log.info("✅ 성능 비교 완료 - MySQL: {}ms, ElasticSearch: {}ms, 개선율: {}%",
                mysqlResult.getAverageResponseTime(),
                elasticResult.getAverageResponseTime(),
                result.getPerformanceGain().getResponseTimeGainPercent());

        return GlobalResponse.success(result);
    }

    @PostMapping("/generate-dummy-data")
    @Operation(summary = "대용량 더미 데이터 생성")
    public GlobalResponse<String> generateLargeDummyData(
            @RequestParam(defaultValue = "100000") int postCount,
            @RequestParam(defaultValue = "500000") int commentCount) {

        log.info("🚀 대용량 더미 데이터 생성 시작 - 게시글: {}개, 댓글: {}개", postCount, commentCount);

        // 비동기 실행
        CompletableFuture.runAsync(() -> {
            try {
                performanceTestService.generateLargeDummyData(postCount, commentCount);
                log.info("✅ 더미 데이터 생성 완료");
            } catch (Exception e) {
                log.error("❌ 더미 데이터 생성 실패", e);
            }
        });

        return GlobalResponse.success(
                String.format("더미 데이터 생성 시작 - 게시글: %d개, 댓글: %d개 (백그라운드 실행)", postCount, commentCount)
        );
    }

    @PostMapping("/load-test")
    @Operation(summary = "부하 테스트 (동시 검색 요청)")
    public GlobalResponse<LoadTestResult> performLoadTest(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1000") int concurrentUsers,
            @RequestParam(defaultValue = "10") int requestsPerUser,
            @RequestParam(defaultValue = "elasticsearch") String searchEngine) {

        log.info("🚀 부하 테스트 시작 - 키워드: {}, 동시 사용자: {}, 엔진: {}",
                keyword, concurrentUsers, searchEngine);

        PostSearchConditionDto condition = new PostSearchConditionDto();
        condition.setKeyword(keyword);
        condition.setSearchType(PostSearchConditionDto.SearchType.ALL);

        LoadTestResult result = loadTestService.performConcurrentSearchTest(
                condition, concurrentUsers, requestsPerUser, searchEngine);

        log.info("✅ 부하 테스트 완료 - 평균 응답시간: {}ms, 처리량: {} req/s, 에러율: {}%",
                result.getAverageResponseTime(), result.getThroughput(), result.getErrorRate());

        return GlobalResponse.success(result);
    }

    @PostMapping("/stress-test")
    @Operation(summary = "스트레스 테스트 (점진적 부하 증가)")
    public GlobalResponse<LoadTestResult> performStressTest(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "100") int startUsers,
            @RequestParam(defaultValue = "2000") int maxUsers,
            @RequestParam(defaultValue = "200") int incrementUsers) {

        log.info("📈 스트레스 테스트 시작 - {}명부터 {}명까지 점진적 증가", startUsers, maxUsers);

        PostSearchConditionDto condition = new PostSearchConditionDto();
        condition.setKeyword(keyword);
        condition.setSearchType(PostSearchConditionDto.SearchType.ALL);

        LoadTestResult result = loadTestService.performStressTest(
                condition, startUsers, maxUsers, incrementUsers, 30);

        return GlobalResponse.success(result);
    }

    @GetMapping("/system-stats")
    @Operation(summary = "시스템 통계 조회")
    public GlobalResponse<SystemStats> getSystemStats() {
        SystemStats stats = performanceTestService.getSystemStats();
        return GlobalResponse.success(stats);
    }

    @GetMapping("/system-resources")
    @Operation(summary = "시스템 리소스 모니터링")
    public GlobalResponse<SystemResourceInfo> getSystemResources() {
        SystemResourceInfo resourceInfo = loadTestService.getSystemResourceInfo();
        return GlobalResponse.success(resourceInfo);
    }

    @PostMapping("/compare-all")
    @Operation(summary = "종합 성능 비교 (검색 + 부하 테스트)")
    public GlobalResponse<ComprehensiveTestResult> comprehensiveTest(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "500") int searchTestCount,
            @RequestParam(defaultValue = "500") int concurrentUsers) {

        log.info("🎯 종합 성능 테스트 시작 - 키워드: {}", keyword);

        // 1. 검색 성능 비교
        PerformanceCompareResult searchComparison = compareSearchPerformance(keyword, 10, searchTestCount).getData();

        // 2. MySQL 부하 테스트
        LoadTestResult mysqlLoadTest = performLoadTest(keyword, concurrentUsers, 5, "mysql").getData();

        // 3. ElasticSearch 부하 테스트
        LoadTestResult elasticLoadTest = performLoadTest(keyword, concurrentUsers, 5, "elasticsearch").getData();

        ComprehensiveTestResult result = ComprehensiveTestResult.builder()
                .keyword(keyword)
                .searchComparison(searchComparison)
                .mysqlLoadTest(mysqlLoadTest)
                .elasticSearchLoadTest(elasticLoadTest)
                .build();

        return GlobalResponse.success(result);
    }

    private PerformanceGain calculatePerformanceGain(PerformanceTestResult mysql, PerformanceTestResult elastic) {
        double responseTimeGain = ((double) (mysql.getAverageResponseTime() - elastic.getAverageResponseTime())
                / mysql.getAverageResponseTime()) * 100;

        double throughputGain = ((double) (elastic.getThroughput() - mysql.getThroughput())
                / mysql.getThroughput()) * 100;

        return PerformanceGain.builder()
                .responseTimeGainPercent(Math.round(responseTimeGain * 100.0) / 100.0)
                .throughputGainPercent(Math.round(throughputGain * 100.0) / 100.0)
                .mysqlAvgResponseTime(mysql.getAverageResponseTime())
                .elasticSearchAvgResponseTime(elastic.getAverageResponseTime())
                .build();
    }
}