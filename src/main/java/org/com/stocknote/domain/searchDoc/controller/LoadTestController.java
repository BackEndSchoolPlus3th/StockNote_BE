package org.com.stocknote.domain.searchDoc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.post.dto.PostSearchConditionDto;
import org.com.stocknote.domain.searchDoc.dto.LoadTestResult;
import org.com.stocknote.domain.searchDoc.dto.SystemResourceInfo;
import org.com.stocknote.domain.searchDoc.service.LoadTestService;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/load-test")
@RequiredArgsConstructor
@Tag(name = "부하 테스트 API", description = "동시 접속 부하 테스트")
@Slf4j
public class LoadTestController {

    private final LoadTestService loadTestService;

    @PostMapping("/concurrent-search")
    @Operation(summary = "동시 검색 부하 테스트")
    public GlobalResponse<LoadTestResult> concurrentSearchTest(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1000") int concurrentUsers,
            @RequestParam(defaultValue = "10") int requestsPerUser,
            @RequestParam(defaultValue = "mysql") String searchEngine) {

        log.info("🚀 동시 검색 부하 테스트 시작 - 키워드: {}, 동시 사용자: {}, 엔진: {}",
                keyword, concurrentUsers, searchEngine);

        PostSearchConditionDto condition = new PostSearchConditionDto();
        condition.setKeyword(keyword);
        condition.setSearchType(PostSearchConditionDto.SearchType.ALL);

        LoadTestResult result = loadTestService.performConcurrentSearchTest(
                condition, concurrentUsers, requestsPerUser, searchEngine);

        log.info("✅ 부하 테스트 완료 - 평균 응답시간: {}ms, 처리량: {} req/s",
                result.getAverageResponseTime(), result.getThroughput());

        return GlobalResponse.success(result);
    }

    @PostMapping("/stress-test")
    @Operation(summary = "스트레스 테스트 (점진적 부하 증가)")
    public GlobalResponse<LoadTestResult> stressTest(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "100") int startUsers,
            @RequestParam(defaultValue = "2000") int maxUsers,
            @RequestParam(defaultValue = "100") int incrementUsers,
            @RequestParam(defaultValue = "30") int testDurationSeconds) {

        log.info("📈 스트레스 테스트 시작 - {}명부터 {}명까지 점진적 증가", startUsers, maxUsers);

        PostSearchConditionDto condition = new PostSearchConditionDto();
        condition.setKeyword(keyword);
        condition.setSearchType(PostSearchConditionDto.SearchType.ALL);

        LoadTestResult result = loadTestService.performStressTest(
                condition, startUsers, maxUsers, incrementUsers, testDurationSeconds);

        return GlobalResponse.success(result);
    }

    @GetMapping("/system-resources")
    @Operation(summary = "시스템 리소스 모니터링")
    public GlobalResponse<SystemResourceInfo> getSystemResources() {
        SystemResourceInfo resourceInfo = loadTestService.getSystemResourceInfo();
        return GlobalResponse.success(resourceInfo);
    }
}