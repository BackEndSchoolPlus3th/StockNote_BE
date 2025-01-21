package org.com.stocknote.global.kis;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class KisKeyManager {

    private final RestTemplate restTemplate;
    private String accessToken;
    private LocalDateTime tokenExpirationTime;
    private String websocketApprovalKey;

    @Getter
    @Value("${korea-investment-api.appkey}")
    private String appKey;

    @Getter
    @Value("${korea-investment-api.appsecret}")
    private String appSecret;


    public KisKeyManager(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 1. Access Token 발급 또는 재발급
     */
    public synchronized String getAccessToken() {
        if (accessToken == null || isTokenExpired()) {
            requestNewAccessToken(); // 토큰 만료 시에만 새로 발급
        }
        return accessToken;
    }
    private boolean isTokenExpired() {
        return tokenExpirationTime == null || LocalDateTime.now().isAfter(tokenExpirationTime);
    }


    private void requestNewAccessToken() {
        String url = "https://openapivts.koreainvestment.com:29443/oauth2/tokenP";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 Body 구성
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "client_credentials");
        body.put("appkey", appKey);  // 실제 appKey로 변경
        body.put("appsecret", appSecret); // 실제 appSecret로 변경

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            // API 호출
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // 응답 처리
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                this.accessToken = (String) responseBody.get("access_token");
                long expiresIn = ((Number) responseBody.get("expires_in")).longValue();
                this.tokenExpirationTime = LocalDateTime.now().plusSeconds(expiresIn);

                System.out.println("새로운 Access Token 발급됨: " + accessToken);
            } else {
                throw new RuntimeException("토큰 발급 응답이 비어있습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Access Token 발급 실패: " + e.getMessage(), e);
        }
    }



    /**
     * 2. WebSocket 접속 키 발급
     */
    public synchronized String getWebSocketApprovalKey() {
        if (websocketApprovalKey == null) {
            requestWebSocketApprovalKey();
        }
        return websocketApprovalKey;
    }

    private void requestWebSocketApprovalKey() {
        String url = "https://openapi.koreainvestment.com:9443/oauth2/Approval";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "client_credentials");
        body.put("appkey", appKey);
        body.put("secretkey", appSecret);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            this.websocketApprovalKey = (String) responseBody.get("approval_key");
        } else {
            throw new RuntimeException("Failed to retrieve WebSocket approval key.");
        }
    }

}
