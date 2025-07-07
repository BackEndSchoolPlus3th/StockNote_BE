package org.com.stocknote.domain.searchDoc.service;

import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.searchDoc.dto.SystemResourceInfo;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;

@Service
@Slf4j
public class SystemMonitoringService {

    public SystemResourceInfo getCurrentSystemInfo() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            Runtime runtime = Runtime.getRuntime();

            // CPU 사용률 측정 (다중 방법 시도)
            double cpuUsage = getCpuUsage(osBean);

            // 메모리 정보 수집
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;

            // 힙 메모리 정보도 수집
            long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
            long heapMax = memoryBean.getHeapMemoryUsage().getMax();

            // 실제 사용 메모리는 더 정확한 값 사용
            long actualUsedMemory = Math.max(usedMemory, heapUsed);
            long actualMaxMemory = Math.max(maxMemory, heapMax);

            double memoryUsage = actualMaxMemory > 0 ?
                    (double) actualUsedMemory / actualMaxMemory * 100 : 0;

            return SystemResourceInfo.builder()
                    .cpuUsagePercent(Math.round(cpuUsage * 100.0) / 100.0)
                    .memoryUsagePercent(Math.round(memoryUsage * 100.0) / 100.0)
                    .totalMemoryMB(actualMaxMemory / (1024 * 1024))
                    .usedMemoryMB(actualUsedMemory / (1024 * 1024))
                    .freeMemoryMB((actualMaxMemory - actualUsedMemory) / (1024 * 1024))
                    .availableProcessors(runtime.availableProcessors())
                    .build();

        } catch (Exception e) {
            log.error("시스템 리소스 정보 수집 실패", e);
            return getDefaultSystemInfo();
        }
    }

    private double getCpuUsage(OperatingSystemMXBean osBean) {
        try {
            // Java 9+ com.sun.management.OperatingSystemMXBean 사용
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunOsBean =
                        (com.sun.management.OperatingSystemMXBean) osBean;

                // 프로세스 CPU 사용률 시도
                double processCpu = sunOsBean.getProcessCpuLoad();
                if (processCpu >= 0) {
                    return processCpu * 100;
                }

                // 시스템 전체 CPU 사용률 시도
                double systemCpu = sunOsBean.getSystemCpuLoad();
                if (systemCpu >= 0) {
                    return systemCpu * 100;
                }
            }

            // deprecated 메서드 사용 안함 - 기본값 반환
            log.debug("CPU 사용률 측정 불가능, 기본값 사용");
            return 0.0;

        } catch (Exception e) {
            log.warn("CPU 사용률 측정 실패: {}", e.getMessage());
            return 0.0;
        }
    }

    private SystemResourceInfo getDefaultSystemInfo() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        return SystemResourceInfo.builder()
                .cpuUsagePercent(0.0)
                .memoryUsagePercent((double) usedMemory / maxMemory * 100)
                .totalMemoryMB(maxMemory / (1024 * 1024))
                .usedMemoryMB(usedMemory / (1024 * 1024))
                .freeMemoryMB(freeMemory / (1024 * 1024))
                .availableProcessors(runtime.availableProcessors())
                .build();
    }
}