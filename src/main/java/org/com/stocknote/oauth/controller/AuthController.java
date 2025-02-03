package org.com.stocknote.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.oauth.service.OAuth2TokenService;
import org.com.stocknote.oauth.token.TokenProvider;
import org.com.stocknote.oauth.token.entity.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "스웨거 로그인 API", description = "Google, Kakao 로그인 API")
public class AuthController {

    private final OAuth2TokenService oAuth2TokenService;
    private final TokenProvider tokenProvider;

    @GetMapping("/google/manual")
    @Operation(summary = "수동 구글 로그인")
    public ResponseEntity<?> googleManualRedirect(@RequestParam String code) {
        // 단순히 code를 확인만 하고,
        // 이후에 '/auth/google/token' 같은 API 호출을 유도해도 되고,
        // 여기서 바로 처리해도 됩니다.

        return ResponseEntity.ok(
                "수동 구글 로그인 완료! 전달받은 code: " + code +
                        "\n이 코드를 이용해 '/auth/google/token?code=...' 호출해서 JWT 발급을 받으세요."
        );
    }

    /**
     * 2) 실제로 code 로 AccessToken 발급 + DB저장 + JWT 발급을 수행하는 엔드포인트
     *
     *    클라이언트(Swagger 등)에서 /auth/google/manual 에서 얻은 code를 가지고
     *    POST /auth/google/token?code=... 로 요청하면,
     *    서버가 code->AccessToken->UserInfo->회원DB->JWT 발급 과정을 처리
     */
    @PostMapping("/google/token")
    @Operation(summary = "구글 로그인(토큰 발급용)")
    public ResponseEntity<?> getGoogleToken(@RequestParam String code) {
        // (A) code 로 구글 AccessToken + 유저정보 획득
        OAuth2User oAuth2User = oAuth2TokenService.processOAuth2User("googleManual", code);

        // (B) DB 저장은 processOAuth2User() 내부에서
        //     customOAuth2UserService.getOrSave(...) 로 처리됨
        //     신규이면 회원가입, 기존이면 findByEmail

        // (C) 이제 JWT 발급
        String email = oAuth2User.getAttribute("email");
        Authentication auth = new UsernamePasswordAuthenticationToken(
                email, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        String accessToken = tokenProvider.generateAccessToken(auth);
        tokenProvider.generateRefreshToken(auth, accessToken);

        // (D) 발급한 JWT를 응답 (또는 필요하면 JSON 형태 변환)
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }
}
