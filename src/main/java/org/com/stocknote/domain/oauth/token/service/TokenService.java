package org.com.stocknote.domain.oauth.token.service;

import org.com.stocknote.domain.oauth.token.entity.Token;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token.expire-time}")
    private long refreshTokenExpireTime;

    public TokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveOrUpdate(String username, String refreshToken, String accessToken) {
        // Redis에 토큰 저장
        // refreshToken을 저장하고, 해당 사용자의 토큰과 연결
        redisTemplate.opsForValue().set(username + ":refresh-token", refreshToken, refreshTokenExpireTime);
        redisTemplate.opsForValue().set(username + ":access-token", accessToken);
    }

    public void updateToken(String newAccessToken, Token token) {
        String username = token.getUsername(); // Token 클래스에서 username을 가져오는 방법
        String newRefreshToken = token.getRefreshToken(); // 기존 리프레시 토큰을 그대로 사용

        // 새로 발급된 액세스 토큰으로 업데이트
        this.saveOrUpdate(username, newRefreshToken, newAccessToken);
    }


    public Token findByAccessTokenOrThrow(String accessToken) {
        // Redis에서 토큰 정보 찾기
        String storedToken = redisTemplate.opsForValue().get(accessToken);

        if (storedToken == null) {
            throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        }

        // 토큰이 존재하면 반환
        return new Token(accessToken, storedToken); // 예시로 Token 클래스 사용
    }

    public String getRefreshToken(String username) {
        // Redis에서 refreshToken을 가져옴
        return redisTemplate.opsForValue().get(username + ":refresh-token");
    }

    public void deleteToken(String username) {
        // Redis에서 토큰 삭제
        redisTemplate.delete(username + ":refresh-token");
        redisTemplate.delete(username + ":access-token");
    }
}