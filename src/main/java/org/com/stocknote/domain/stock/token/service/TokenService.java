package org.com.stocknote.domain.stock.token.service;

import jakarta.annotation.PostConstruct;
import org.com.stocknote.domain.stock.token.dto.TokenRequestDto;
import org.com.stocknote.domain.stock.token.dto.TokenResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TokenService {
    private WebClient webClient;
    private String cachedToken;

    @Value("${kis.app-key}")
    private String appKey;
    @Value("${kis.app-secret}")
    private String appSecret;
    @Value("${kis.token-base-url}")
    private String tokenBaseUrl;

    // WebClient 초기화를 @PostConstruct에서 수행
    @PostConstruct
    public void initWebClient() {
        this.webClient = WebClient.builder()
                .baseUrl(tokenBaseUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.cachedToken = generateNewToken();
    }

    // 저장된 토큰 발급
    public String getAccessToken() {
        if (cachedToken == null) {
            cachedToken = generateNewToken();
        }

        return cachedToken;
    }

    // 접근 토큰 발급
    public String generateNewToken() {
        TokenRequestDto tokenRequestDto = new TokenRequestDto("client_credentials", appKey, appSecret);

        return webClient.post()
                .uri("/oauth2/tokenP")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tokenRequestDto)
                .retrieve()
                .bodyToMono(TokenResponseDto.class)
                .map(TokenResponseDto::getAccessToken)
                .block();
    }

    // 토큰 자동 갱신 로직
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)  // 24시간마다
    public void refreshToken() {
        this.cachedToken = generateNewToken();
    }
}
