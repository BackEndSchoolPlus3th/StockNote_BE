package org.com.stocknote.domain.searchDoc.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceGain {
    private double responseTimeGainPercent;      // 응답시간 개선율 (%)
    private double throughputGainPercent;        // 처리량 개선율 (%)
    private double mysqlAvgResponseTime;         // MySQL 평균 응답시간
    private double elasticSearchAvgResponseTime; // ElasticSearch 평균 응답시간

    // 개선 정도를 텍스트로 표현
    public String getPerformanceLevel() {
        if (responseTimeGainPercent >= 50) {
            return "EXCELLENT";  // 50% 이상 개선
        } else if (responseTimeGainPercent >= 20) {
            return "GOOD";       // 20-50% 개선
        } else if (responseTimeGainPercent >= 5) {
            return "MODERATE";   // 5-20% 개선
        } else if (responseTimeGainPercent >= 0) {
            return "MARGINAL";   // 0-5% 개선
        } else {
            return "DEGRADED";   // 성능 저하
        }
    }
}