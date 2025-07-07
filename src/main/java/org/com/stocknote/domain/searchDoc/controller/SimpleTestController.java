package org.com.stocknote.domain.searchDoc.controller;

import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public/test")
@Slf4j
public class SimpleTestController {

    @GetMapping("/health")
    public GlobalResponse<String> healthCheck() {
        log.info("Health check 요청됨");
        return GlobalResponse.success("서버가 정상 작동 중입니다 - " + LocalDateTime.now());
    }

    @GetMapping("/system-info")
    public GlobalResponse<Map<String, Object>> getBasicSystemInfo() {
        try {
            log.info("기본 시스템 정보 요청됨");

            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> systemInfo = new HashMap<>();

            systemInfo.put("availableProcessors", runtime.availableProcessors());
            systemInfo.put("totalMemoryMB", runtime.totalMemory() / (1024 * 1024));
            systemInfo.put("freeMemoryMB", runtime.freeMemory() / (1024 * 1024));
            systemInfo.put("maxMemoryMB", runtime.maxMemory() / (1024 * 1024));
            systemInfo.put("timestamp", LocalDateTime.now());
            systemInfo.put("status", "OK");

            log.info("시스템 정보 수집 완료: {}", systemInfo);
            return GlobalResponse.success(systemInfo);

        } catch (Exception e) {
            log.error("시스템 정보 수집 실패", e);
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("error", e.getMessage());
            errorInfo.put("timestamp", LocalDateTime.now());
            return GlobalResponse.success(errorInfo);
        }
    }
}