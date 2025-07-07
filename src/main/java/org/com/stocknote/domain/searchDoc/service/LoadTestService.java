package org.com.stocknote.domain.searchDoc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.post.dto.PostSearchConditionDto;
import org.com.stocknote.domain.post.repository.PostRepository;
import org.com.stocknote.domain.searchDoc.dto.LoadTestResult;
import org.com.stocknote.domain.searchDoc.dto.SystemResourceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoadTestService {

    private final PostRepository postRepository;

    @Autowired(required = false)
    private SearchDocService searchDocService;

    @Autowired(required = false)
    private SystemMonitoringService systemMonitoringService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(50);

    public LoadTestResult performConcurrentSearchTest(PostSearchConditionDto condition,
                                                      int concurrentUsers,
                                                      int requestsPerUser,
                                                      String searchEngine) {

        log.info("🚀 부하 테스트 시작 - 엔진: {}, 동시 사용자: {}, 사용자당 요청: {}",
                searchEngine, concurrentUsers, requestsPerUser);

        Pageable pageable = PageRequest.of(0, 10);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completeLatch = new CountDownLatch(concurrentUsers);

        AtomicLong totalResponseTime = new AtomicLong(0);
        AtomicLong successfulRequests = new AtomicLong(0);
        AtomicLong failedRequests = new AtomicLong(0);
        List<Long> responseTimes = new CopyOnWriteArrayList<>();

        long testStartTime = System.currentTimeMillis();

        // 동시 사용자 시뮬레이션
        IntStream.range(0, concurrentUsers).forEach(userId -> {
            executorService.submit(() -> {
                try {
                    // 모든 스레드가 동시에 시작하도록 대기
                    startLatch.await();

                    // 각 사용자가 여러 요청 수행
                    for (int request = 0; request < requestsPerUser; request++) {
                        long requestStart = System.nanoTime();

                        try {
                            if ("elasticsearch".equalsIgnoreCase(searchEngine) && searchDocService != null) {
                                searchDocService.searchPosts(condition, pageable);
                            } else {
                                // MySQL 검색
                                postRepository.findByTitleContainingOrBodyContaining(
                                        condition.getKeyword(), condition.getKeyword(), pageable);
                            }

                            long requestEnd = System.nanoTime();
                            long responseTime = (requestEnd - requestStart) / 1_000_000; // ms

                            responseTimes.add(responseTime);
                            totalResponseTime.addAndGet(responseTime);
                            successfulRequests.incrementAndGet();

                        } catch (Exception e) {
                            failedRequests.incrementAndGet();
                            log.warn("검색 요청 실패 - 사용자: {}, 요청: {}, 에러: {}",
                                    userId, request, e.getMessage());
                        }

                        // 사용자별 요청 간격 (100ms)
                        Thread.sleep(100);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    completeLatch.countDown();
                }
            });
        });

        // 테스트 시작
        log.info("🚀 부하 테스트 시작 - 동시 사용자: {}, 사용자당 요청: {}", concurrentUsers, requestsPerUser);
        startLatch.countDown();

        try {
            // 최대 10분 대기
            if (!completeLatch.await(10, TimeUnit.MINUTES)) {
                log.warn("⚠️ 부하 테스트 시간 초과");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long testEndTime = System.currentTimeMillis();
        long totalTestTime = testEndTime - testStartTime;

        // 결과 계산
        long totalRequests = successfulRequests.get() + failedRequests.get();
        double averageResponseTime = successfulRequests.get() > 0 ?
                (double) totalResponseTime.get() / successfulRequests.get() : 0;
        double throughput = (double) successfulRequests.get() / (totalTestTime / 1000.0);

        // 응답시간 통계
        responseTimes.sort(Long::compareTo);
        long minResponseTime = responseTimes.isEmpty() ? 0 : responseTimes.get(0);
        long maxResponseTime = responseTimes.isEmpty() ? 0 : responseTimes.get(responseTimes.size() - 1);
        long p95ResponseTime = responseTimes.isEmpty() ? 0 :
                responseTimes.get((int) (responseTimes.size() * 0.95));
        long p99ResponseTime = responseTimes.isEmpty() ? 0 :
                responseTimes.get((int) (responseTimes.size() * 0.99));

        return LoadTestResult.builder()
                .searchEngine(searchEngine)
                .concurrentUsers(concurrentUsers)
                .requestsPerUser(requestsPerUser)
                .totalRequests(totalRequests)
                .successfulRequests(successfulRequests.get())
                .failedRequests(failedRequests.get())
                .testDurationMs(totalTestTime)
                .averageResponseTime(Math.round(averageResponseTime * 100.0) / 100.0)
                .minResponseTime(minResponseTime)
                .maxResponseTime(maxResponseTime)
                .p95ResponseTime(p95ResponseTime)
                .p99ResponseTime(p99ResponseTime)
                .throughput(Math.round(throughput * 100.0) / 100.0)
                .errorRate(totalRequests > 0 ? (double) failedRequests.get() / totalRequests * 100 : 0)
                .build();
    }

    public LoadTestResult performStressTest(PostSearchConditionDto condition,
                                            int startUsers,
                                            int maxUsers,
                                            int incrementUsers,
                                            int testDurationSeconds) {

        log.info("📈 스트레스 테스트 시작 - {}명부터 {}명까지", startUsers, maxUsers);

        List<LoadTestResult> phaseResults = new ArrayList<>();

        for (int currentUsers = startUsers; currentUsers <= maxUsers; currentUsers += incrementUsers) {
            log.info("🔄 스트레스 테스트 단계 - 동시 사용자: {}명", currentUsers);

            LoadTestResult phaseResult = performConcurrentSearchTest(
                    condition, currentUsers, 5, "mysql");

            phaseResults.add(phaseResult);

            // 시스템 안정화를 위한 대기
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            // 에러율이 10% 이상이면 중단
            if (phaseResult.getErrorRate() > 10.0) {
                log.warn("⚠️ 에러율 {}% 초과로 스트레스 테스트 중단", phaseResult.getErrorRate());
                break;
            }
        }

        // 전체 결과 집계
        LoadTestResult bestResult = phaseResults.stream()
                .min((r1, r2) -> Double.compare(r1.getAverageResponseTime(), r2.getAverageResponseTime()))
                .orElse(phaseResults.get(phaseResults.size() - 1));

        LoadTestResult worstResult = phaseResults.stream()
                .max((r1, r2) -> Double.compare(r1.getAverageResponseTime(), r2.getAverageResponseTime()))
                .orElse(phaseResults.get(0));

        return LoadTestResult.builder()
                .searchEngine("mysql")
                .concurrentUsers(maxUsers)
                .requestsPerUser(5)
                .totalRequests(phaseResults.stream().mapToLong(LoadTestResult::getTotalRequests).sum())
                .successfulRequests(phaseResults.stream().mapToLong(LoadTestResult::getSuccessfulRequests).sum())
                .failedRequests(phaseResults.stream().mapToLong(LoadTestResult::getFailedRequests).sum())
                .testDurationMs(phaseResults.stream().mapToLong(LoadTestResult::getTestDurationMs).sum())
                .averageResponseTime(phaseResults.stream().mapToDouble(LoadTestResult::getAverageResponseTime).average().orElse(0))
                .minResponseTime(bestResult.getMinResponseTime())
                .maxResponseTime(worstResult.getMaxResponseTime())
                .p95ResponseTime(phaseResults.stream().mapToLong(LoadTestResult::getP95ResponseTime).max().orElse(0))
                .p99ResponseTime(phaseResults.stream().mapToLong(LoadTestResult::getP99ResponseTime).max().orElse(0))
                .throughput(phaseResults.stream().mapToDouble(LoadTestResult::getThroughput).max().orElse(0))
                .errorRate(phaseResults.stream().mapToDouble(LoadTestResult::getErrorRate).average().orElse(0))
                .build();
    }

    public SystemResourceInfo getSystemResourceInfo() {
        if (systemMonitoringService != null) {
            return systemMonitoringService.getCurrentSystemInfo();
        }

        // 기본 시스템 정보 반환
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

    // 리소스 정리
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}