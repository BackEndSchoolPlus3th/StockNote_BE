package org.com.stocknote.domain.stockApi.stockToken.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.common.subtyping.qual.Bottom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Getter
@Component
public class StockTokenService {
    private final StringRedisTemplate redisTemplate;
    private WebClient tokenWebClient;
    private WebClient websocketWebClient;
    private String accessToken;
    private LocalDateTime tokenExpirationTime;
    private String websocketApprovalKey;
    private final ReentrantLock lock = new ReentrantLock();

    @Value("${kis.app-key}")
    private String appKey;

    @Value("${kis.app-secret}")
    private String appSecret;

    @Value("${kis.index-base-url}")
    private String indexBaseUrl;

    @Value("${kis.token-base-url}")
    private String tokenBaseUrl;

    @Value("${kis.volume-base-url}")
    private String volumeBaseUrl;

    public StockTokenService (StringRedisTemplate redisTemplate, WebClient.Builder webClientBuilder) {
        this.redisTemplate = redisTemplate;
        this.tokenWebClient = webClientBuilder.baseUrl("https://openapi.koreainvestment.com:9443").build();
    }

//    @PostConstruct
    @Bean
    public void webClient() {
        // log.debug("post construct ${StockTokenService}")
        this.tokenWebClient = WebClient.builder()
                .baseUrl(tokenBaseUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.websocketWebClient = WebClient.builder()
                .baseUrl("https://openapi.koreainvestment.com:9443")
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }


    public String getAccessToken() {
        String cachedToken = redisTemplate.opsForValue().get("stocknote:access-token");

        if (cachedToken != null) {
            return cachedToken;  // ✅ Redis에서 저장된 토큰 사용
        }

        if (!lock.tryLock()) {
            return accessToken;  // ✅ 다른 요청이 토큰을 갱신 중이면 기존 토큰 사용
        }

        try {
            return refreshAccessToken();  // ✅ 새 토큰 요청 후 반환
        } finally {
            lock.unlock();
        }
    }


    public synchronized String getWebSocketApprovalKey() {
        if (websocketApprovalKey == null) {
            requestWebSocketApprovalKey();
        }
        return websocketApprovalKey;
    }

    private void requestWebSocketApprovalKey() {
        try {
            Map<String, Object> response = websocketWebClient
                    .post()
                    // 한국 투자증권 rest API, websocket
                    .uri("{domain}/oauth2/Approval")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of(
                            "grant_type", "client_credentials",
                            "appkey", appKey,
                            "secretkey", appSecret
                    ))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("WebSocket approval key request failed: {}", errorBody);
                                        return Mono.error(new RuntimeException("WebSocket approval key 발급 실패: " + errorBody));
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response != null && response.get("approval_key") != null) {
                this.websocketApprovalKey = (String) response.get("approval_key");
                log.debug("WebSocket approval key generated successfully");
            } else {
                throw new RuntimeException("WebSocket approval key 발급 응답이 비어있습니다.");
            }
        } catch (Exception e) {
            log.error("WebSocket approval key request failed", e);
            throw new RuntimeException("WebSocket approval key 발급 실패: " + e.getMessage(), e);
        }
    }

    private String refreshAccessToken() {
        try {
            // 기존 Redis에 저장된 refresh_token 가져오기
            String cachedRefreshToken = redisTemplate.opsForValue().get("stocknote:refresh-token");

            if (cachedRefreshToken != null) {
                return tokenWebClient
                        .post()
                        .uri("/oauth2/tokenP")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of(
                                "grant_type", "refresh_token",
                                "refresh_token", cachedRefreshToken,
                                "appkey", appKey,
                                "appsecret", appSecret
                        ))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .retryWhen(reactor.util.retry.Retry.fixedDelay(3, java.time.Duration.ofSeconds(2)))  // ✅ 3번 재시도, 2초 간격
                        .doOnError(e -> log.error("❌ [ACCESS TOKEN 갱신 실패] {}", e.getMessage()))
                        .blockOptional()
                        .map(this::processTokenResponse)
                        .orElseThrow(() -> new RuntimeException("Access Token 갱신 실패"));
            }

            return requestNewAccessToken();

        } catch (Exception e) {
            log.error("❌ Access Token 갱신 실패: {}", e.getMessage());
            throw new RuntimeException("Access Token 갱신 중 오류 발생", e);
        }
    }

    private String requestNewAccessToken() {
        return tokenWebClient
                .post()
                .uri("/oauth2/tokenP")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "grant_type", "client_credentials",
                        "appkey", appKey,
                        "appsecret", appSecret
                ))
                .retrieve()
                // ParameterizedTypeReference dto
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .retryWhen(reactor.util.retry.Retry.fixedDelay(3, java.time.Duration.ofSeconds(2)))  // ✅ 3번 재시도, 2초 간격
                .doOnError(e -> log.error("❌ [ACCESS TOKEN 신규 발급 실패] {}", e.getMessage()))
                .blockOptional()
                .map(this::processTokenResponse)
                .orElseThrow(() -> new RuntimeException("Access Token 발급 실패"));
    }

    private String processTokenResponse(Map<String, Object> response) {
        if (response != null && response.get("access_token") != null) {
            this.accessToken = (String) response.get("access_token");
            long expiresIn = ((Number) response.get("expires_in")).longValue();
            this.tokenExpirationTime = LocalDateTime.now().plusSeconds(expiresIn);

            // 🚀 5분 전 미리 갱신
            long redisExpireTime = Math.max(expiresIn - 300, 10);

            // Redis에 access_token 저장
            redisTemplate.opsForValue().set("stocknote:access-token", accessToken, redisExpireTime, TimeUnit.SECONDS);

            // refresh_token도 저장 (응답에 포함된 경우)
            if (response.get("refresh_token") != null) {
                String refreshToken = (String) response.get("refresh_token");
                redisTemplate.opsForValue().set("stocknote:refresh-token", refreshToken, 30, TimeUnit.DAYS);
            }

            log.info("🚀 [ACCESS TOKEN 발급 성공] 만료까지: {}초", expiresIn);
            return accessToken;
        } else {
            throw new RuntimeException("Access Token 응답이 비어있습니다.");
        }
    }
}
