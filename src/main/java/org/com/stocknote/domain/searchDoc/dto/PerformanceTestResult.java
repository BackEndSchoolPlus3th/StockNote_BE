package org.com.stocknote.domain.searchDoc.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceTestResult {
    private String searchEngine;
    private double averageResponseTime;  // 평균 응답시간 (ms)
    private long minResponseTime;        // 최소 응답시간 (ms)
    private long maxResponseTime;        // 최대 응답시간 (ms)
    private double throughput;           // 처리량 (requests/second)
    private long totalResults;           // 검색 결과 총 개수
    private int testCount;               // 테스트 실행 횟수
}