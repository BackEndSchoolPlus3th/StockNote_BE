package org.com.stocknote.domain.searchDoc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.post.dto.PostSearchConditionDto;
import org.com.stocknote.domain.post.repository.PostRepository;
import org.com.stocknote.domain.searchDoc.dto.LoadTestResult;
import org.com.stocknote.domain.searchDoc.dto.SystemResourceInfo;
import org.com.stocknote.domain.searchDoc.service.LoadTestService;
import org.com.stocknote.domain.searchDoc.service.PerformanceTestService;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/public/performance")
@Tag(name = "공개 성능 테스트 API", description = "인증 없이 사용 가능한 성능 테스트")
@Slf4j
public class PublicPerformanceController {

    @Autowired(required = false)
    private PerformanceTestService performanceTestService;

    @Autowired(required = false)
    private LoadTestService loadTestService;

    @Autowired
    private PostRepository postRepository;

    @PostMapping("/load-test")
    @Operation(summary = "부하 테스트")
    public GlobalResponse<Map<String, Object>> performLoadTest(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "50") int concurrentUsers,
            @RequestParam(defaultValue = "5") int requestsPerUser,
            @RequestParam(defaultValue = "mysql") String searchEngine) {

        try {
            log.info("🚀 부하 테스트 요청 - 키워드: {}, 동시 사용자: {}, 엔진: {}",
                    keyword, concurrentUsers, searchEngine);

            if (loadTestService == null) {
                log.warn("LoadTestService Bean이 없음, 기본 테스트 실행");
                return performBasicLoadTest(keyword, concurrentUsers, requestsPerUser, searchEngine);
            }

            PostSearchConditionDto condition = new PostSearchConditionDto();
            condition.setKeyword(keyword);
            condition.setSearchType(PostSearchConditionDto.SearchType.ALL);

            LoadTestResult result = loadTestService.performConcurrentSearchTest(
                    condition, concurrentUsers, requestsPerUser, searchEngine);

            Map<String, Object> response = new HashMap<>();
            response.put("searchEngine", result.getSearchEngine());
            response.put("concurrentUsers", result.getConcurrentUsers());
            response.put("totalRequests", result.getTotalRequests());
            response.put("successfulRequests", result.getSuccessfulRequests());
            response.put("averageResponseTime", result.getAverageResponseTime());
            response.put("throughput", result.getThroughput());
            response.put("errorRate", result.getErrorRate());
            response.put("performanceGrade", result.getPerformanceGrade());

            return GlobalResponse.success(response);

        } catch (Exception e) {
            log.error("부하 테스트 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "부하 테스트 실패: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return GlobalResponse.success(errorResponse);
        }
    }

    private GlobalResponse<Map<String, Object>> performBasicLoadTest(String keyword,
                                                                     int concurrentUsers, int requestsPerUser, String searchEngine) {
        try {
            log.info("기본 부하 테스트 실행");

            Pageable pageable = PageRequest.of(0, 10);
            long startTime = System.currentTimeMillis();

            // 기본적인 검색 테스트
            int successCount = 0;
            int errorCount = 0;
            long totalResponseTime = 0;

            for (int i = 0; i < Math.min(concurrentUsers * requestsPerUser, 100); i++) {
                try {
                    long requestStart = System.currentTimeMillis();

                    if ("elasticsearch".equalsIgnoreCase(searchEngine)) {
                        // ElasticSearch는 서비스가 없으면 스킵
                        log.warn("ElasticSearch 서비스 없음, MySQL로 대체");
                    }

                    // MySQL 검색
                    postRepository.findByTitleContainingOrBodyContaining(keyword, keyword, pageable);

                    long requestEnd = System.currentTimeMillis();
                    totalResponseTime += (requestEnd - requestStart);
                    successCount++;

                } catch (Exception e) {
                    errorCount++;
                    log.warn("검색 요청 실패: {}", e.getMessage());
                }
            }

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            int totalRequests = successCount + errorCount;

            Map<String, Object> result = new HashMap<>();
            result.put("searchEngine", "MySQL (기본 모드)");
            result.put("concurrentUsers", concurrentUsers);
            result.put("requestsPerUser", requestsPerUser);
            result.put("totalRequests", totalRequests);
            result.put("successfulRequests", successCount);
            result.put("failedRequests", errorCount);
            result.put("testDurationMs", totalTime);
            result.put("averageResponseTime", totalRequests > 0 ? (double) totalResponseTime / successCount : 0);
            result.put("throughput", totalTime > 0 ? (double) successCount / (totalTime / 1000.0) : 0);
            result.put("errorRate", totalRequests > 0 ? (double) errorCount / totalRequests * 100 : 0);
            result.put("mode", "BASIC");
            result.put("timestamp", LocalDateTime.now());

            return GlobalResponse.success(result);

        } catch (Exception e) {
            log.error("기본 부하 테스트 실패", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "기본 부하 테스트 실패: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return GlobalResponse.success(errorResponse);
        }
    }

    @GetMapping("/system-resources")
    @Operation(summary = "시스템 리소스 모니터링")
    public GlobalResponse<Map<String, Object>> getSystemResources() {
        try {
            log.info("시스템 리소스 조회 요청");

            if (loadTestService == null) {
                log.warn("LoadTestService Bean이 없음, 기본 정보 반환");
                return getBasicSystemInfo();
            }

            SystemResourceInfo resourceInfo = loadTestService.getSystemResourceInfo();

            Map<String, Object> response = new HashMap<>();
            response.put("cpuUsagePercent", resourceInfo.getCpuUsagePercent());
            response.put("memoryUsagePercent", resourceInfo.getMemoryUsagePercent());
            response.put("totalMemoryMB", resourceInfo.getTotalMemoryMB());
            response.put("usedMemoryMB", resourceInfo.getUsedMemoryMB());
            response.put("freeMemoryMB", resourceInfo.getFreeMemoryMB());
            response.put("availableProcessors", resourceInfo.getAvailableProcessors());
            response.put("systemStatus", resourceInfo.getSystemStatus());
            response.put("timestamp", resourceInfo.getTimestamp());

            return GlobalResponse.success(response);

        } catch (Exception e) {
            log.error("시스템 리소스 조회 실패", e);
            return getBasicSystemInfo();
        }
    }

    private GlobalResponse<Map<String, Object>> getBasicSystemInfo() {
        try {
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> systemInfo = new HashMap<>();

            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;

            systemInfo.put("cpuUsagePercent", 0.0);
            systemInfo.put("memoryUsagePercent", maxMemory > 0 ? (double) usedMemory / maxMemory * 100 : 0);
            systemInfo.put("totalMemoryMB", maxMemory / (1024 * 1024));
            systemInfo.put("usedMemoryMB", usedMemory / (1024 * 1024));
            systemInfo.put("freeMemoryMB", freeMemory / (1024 * 1024));
            systemInfo.put("availableProcessors", runtime.availableProcessors());
            systemInfo.put("timestamp", LocalDateTime.now());
            systemInfo.put("systemStatus", "BASIC_MODE");

            return GlobalResponse.success(systemInfo);

        } catch (Exception e) {
            log.error("기본 시스템 정보 조회 실패", e);
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("error", "시스템 정보 조회 실패: " + e.getMessage());
            errorInfo.put("timestamp", LocalDateTime.now());
            return GlobalResponse.success(errorInfo);
        }
    }

    @PostMapping("/generate-dummy-data")
    @Operation(summary = "더미 데이터 생성")
    public GlobalResponse<String> generateDummyData(
            @RequestParam(defaultValue = "1000") int postCount,
            @RequestParam(defaultValue = "5000") int commentCount) {

        try {
            if (performanceTestService == null) {
                return GlobalResponse.success("PerformanceTestService를 사용할 수 없습니다. ElasticSearch 설정을 확인해주세요.");
            }

            log.info("🚀 더미 데이터 생성 시작 - 게시글: {}개, 댓글: {}개", postCount, commentCount);

            CompletableFuture.runAsync(() -> {
                try {
                    performanceTestService.generateLargeDummyData(postCount, commentCount);
                    log.info("✅ 더미 데이터 생성 완료");
                } catch (Exception e) {
                    log.error("❌ 더미 데이터 생성 실패", e);
                }
            });

            return GlobalResponse.success(
                    String.format("더미 데이터 생성 시작 - 게시글: %d개, 댓글: %d개", postCount, commentCount)
            );

        } catch (Exception e) {
            log.error("더미 데이터 생성 요청 실패", e);
            return GlobalResponse.success("더미 데이터 생성 실패: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    @Operation(summary = "서비스 상태 확인")
    public GlobalResponse<Map<String, Object>> getServiceStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("performanceTestService", performanceTestService != null ? "AVAILABLE" : "NOT_AVAILABLE");
        status.put("loadTestService", loadTestService != null ? "AVAILABLE" : "NOT_AVAILABLE");
        status.put("postRepository", postRepository != null ? "AVAILABLE" : "NOT_AVAILABLE");
        status.put("timestamp", LocalDateTime.now());

        return GlobalResponse.success(status);
    }
}