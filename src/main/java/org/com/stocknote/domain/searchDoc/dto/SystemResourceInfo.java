package org.com.stocknote.domain.searchDoc.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemResourceInfo {
    private double cpuUsagePercent;        // CPU 사용률 (%)
    private double memoryUsagePercent;     // 메모리 사용률 (%)
    private long totalMemoryMB;           // 총 메모리 (MB)
    private long usedMemoryMB;            // 사용된 메모리 (MB)
    private long freeMemoryMB;            // 여유 메모리 (MB)
    private int availableProcessors;       // 사용 가능한 프로세서 수

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // 시스템 상태 판단
    public String getSystemStatus() {
        if (cpuUsagePercent > 90 || memoryUsagePercent > 90) {
            return "CRITICAL";       // 임계 상태
        } else if (cpuUsagePercent > 70 || memoryUsagePercent > 70) {
            return "WARNING";        // 경고 상태
        } else if (cpuUsagePercent > 50 || memoryUsagePercent > 50) {
            return "NORMAL";         // 정상 상태
        } else {
            return "OPTIMAL";        // 최적 상태
        }
    }

    // 메모리 사용률 재계산 (getter에서 동적 계산)
    public double getMemoryUsagePercent() {
        if (totalMemoryMB > 0) {
            return (double) usedMemoryMB / totalMemoryMB * 100;
        }
        return memoryUsagePercent;
    }
}