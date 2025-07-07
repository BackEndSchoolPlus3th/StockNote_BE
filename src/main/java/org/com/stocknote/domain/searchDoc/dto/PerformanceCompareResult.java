package org.com.stocknote.domain.searchDoc.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceCompareResult {
    private String keyword;
    private int testCount;
    private PerformanceTestResult mysqlResult;
    private PerformanceTestResult elasticSearchResult;
    private PerformanceGain performanceGain;

    @Builder.Default
    private LocalDateTime testTime = LocalDateTime.now();
}