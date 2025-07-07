package org.com.stocknote.domain.searchDoc.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprehensiveTestResult {
    private String keyword;
    private PerformanceCompareResult searchComparison;
    private LoadTestResult mysqlLoadTest;
    private LoadTestResult elasticSearchLoadTest;

    @Builder.Default
    private LocalDateTime testTime = LocalDateTime.now();

    // 종합 성능 분석
    public String getOverallPerformanceAssessment() {
        double searchImprovement = searchComparison.getPerformanceGain().getResponseTimeGainPercent();
        double loadTestImprovement = calculateLoadTestImprovement();

        if (searchImprovement > 70 && loadTestImprovement > 50) {
            return "EXCELLENT - ElasticSearch가 모든 영역에서 우수한 성능을 보입니다.";
        } else if (searchImprovement > 30 && loadTestImprovement > 20) {
            return "GOOD - ElasticSearch가 대부분 영역에서 개선된 성능을 보입니다.";
        } else if (searchImprovement > 0 && loadTestImprovement > 0) {
            return "FAIR - ElasticSearch가 일부 개선된 성능을 보입니다.";
        } else {
            return "POOR - 성능 개선이 미미하거나 MySQL이 더 나은 성능을 보입니다.";
        }
    }

    private double calculateLoadTestImprovement() {
        if (mysqlLoadTest.getAverageResponseTime() == 0) return 0;

        return ((mysqlLoadTest.getAverageResponseTime() - elasticSearchLoadTest.getAverageResponseTime())
                / mysqlLoadTest.getAverageResponseTime()) * 100;
    }

    // 포트폴리오용 요약
    public String getPortfolioSummary() {
        double searchGain = searchComparison.getPerformanceGain().getResponseTimeGainPercent();
        double throughputGain = searchComparison.getPerformanceGain().getThroughputGainPercent();

        return String.format(
                "ElasticSearch 도입으로 검색 성능 %.1f%% 향상, 처리량 %.1f%% 증가. " +
                        "동시 사용자 %d명 환경에서 안정적인 서비스 제공 가능.",
                searchGain, throughputGain, elasticSearchLoadTest.getConcurrentUsers()
        );
    }
}