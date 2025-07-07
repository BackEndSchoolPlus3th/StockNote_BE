package org.com.stocknote.domain.searchDoc.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoadTestResult {
    private String searchEngine;           // 검색 엔진 (MySQL/ElasticSearch)
    private int concurrentUsers;           // 동시 사용자 수
    private int requestsPerUser;           // 사용자당 요청 수
    private long totalRequests;            // 총 요청 수
    private long successfulRequests;       // 성공한 요청 수
    private long failedRequests;           // 실패한 요청 수
    private long testDurationMs;           // 테스트 소요 시간 (ms)

    // 응답시간 통계
    private double averageResponseTime;    // 평균 응답시간 (ms)
    private long minResponseTime;          // 최소 응답시간 (ms)
    private long maxResponseTime;          // 최대 응답시간 (ms)
    private long p95ResponseTime;          // 95퍼센타일 응답시간 (ms)
    private long p99ResponseTime;          // 99퍼센타일 응답시간 (ms)

    // 성능 지표
    private double throughput;             // 처리량 (requests/second)
    private double errorRate;              // 에러율 (%)

    @Builder.Default
    private LocalDateTime testTime = LocalDateTime.now();

    // 성능 등급 계산
    public String getPerformanceGrade() {
        if (errorRate > 5.0) {
            return "POOR";           // 에러율 5% 초과
        } else if (averageResponseTime > 1000) {
            return "POOR";           // 평균 응답시간 1초 초과
        } else if (averageResponseTime > 500) {
            return "FAIR";           // 평균 응답시간 500ms ~ 1초
        } else if (averageResponseTime > 200) {
            return "GOOD";           // 평균 응답시간 200ms ~ 500ms
        } else {
            return "EXCELLENT";      // 평균 응답시간 200ms 이하
        }
    }

    // 성공률 계산
    public double getSuccessRate() {
        return totalRequests > 0 ?
                (double) successfulRequests / totalRequests * 100 : 0;
    }

    // TPS (Transactions Per Second) 계산
    public double getTPS() {
        return testDurationMs > 0 ?
                (double) successfulRequests / (testDurationMs / 1000.0) : 0;
    }
}