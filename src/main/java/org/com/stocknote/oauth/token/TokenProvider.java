package org.com.stocknote.oauth.token;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.oauth.token.entity.Token;
import org.com.stocknote.oauth.token.service.TokenService;
import org.springframework.security.core.userdetails.User;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TokenProvider {

    @Value("${jwt.key}")
    private String key;
    private SecretKey secretKey;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30L;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L * 24 * 7;
    private static final String KEY_ROLE = "role";
    private final TokenService tokenService;

    @PostConstruct
    private void setSecretKey() {
        secretKey = Keys.hmacShaKeyFor(key.getBytes());
    }

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public Token createTokens(String email) {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList() // 권한
                                                                                             // 정보
                                                                                             // 없이 빈
                                                                                             // 리스트로
                                                                                             // 설정
                );

        // Access Token 생성
        String accessToken = generateAccessToken(authentication);

        // Refresh Token 생성 및 저장
        generateRefreshToken(authentication, accessToken);

        // Token 객체 생성 및 반환
        Token token = new Token(accessToken, null); // Refresh Token 저장은 Redis에서 관리
        token.setUsername(email);
        return token;
    }


    // 1. refresh token 발급
    public void generateRefreshToken(Authentication authentication, String accessToken) {
        String refreshToken = generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
        tokenService.saveOrUpdate(authentication.getName(), refreshToken, accessToken); // redis에 저장
    }

    private String generateToken(Authentication authentication, long expireTime) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining());

        return Jwts.builder().setSubject(authentication.getName()).claim(KEY_ROLE, authorities)
                .setIssuedAt(now).setExpiration(expiredDate)
                .signWith(secretKey, SignatureAlgorithm.HS512).compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

        // 2. security의 User 객체 생성
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections
                .singletonList(new SimpleGrantedAuthority(claims.get(KEY_ROLE).toString()));
    }

    // 3. accessToken 재발급
    public String reissueAccessToken(String accessToken) {
        if (StringUtils.hasText(accessToken)) {
            Token token = tokenService.findByAccessTokenOrThrow(accessToken);
            String refreshToken = token.getRefreshToken();

            if (validateToken(refreshToken)) {
                String reissueAccessToken = generateAccessToken(getAuthentication(refreshToken));
                tokenService.updateToken(reissueAccessToken, token);
                return reissueAccessToken;
            }
        }
        return null;
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        Claims claims = parseClaims(token);
        return claims.getExpiration().after(new Date());
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (MalformedJwtException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (SecurityException e) {
            throw new CustomException(ErrorCode.INVALID_JWT_SIGNATURE);
        }
    }
}
