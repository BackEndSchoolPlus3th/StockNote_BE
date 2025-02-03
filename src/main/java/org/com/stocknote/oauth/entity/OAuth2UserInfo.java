package org.com.stocknote.oauth.entity;

import lombok.Builder;
import org.com.stocknote.domain.member.entity.AuthProvider;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.entity.Role;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;

import java.util.Map;

@Builder
public record OAuth2UserInfo(
        String name,
        String email,
        String profile,
        String provider,
        String providerId
) {

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) { // registration id별로 userInfo 생성
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            default -> throw new CustomException(ErrorCode.ILLEGAL_REGISTRATION_ID);
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
                .provider("google")
                .providerId(attributes.get("sub").toString())
                .build();
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2UserInfo.builder()
                .name((String) profile.get("nickname"))
                .email((String) account.get("email"))
                .profile((String) profile.get("profile_image_url"))
                .provider("kakao")
                .providerId(attributes.get("id").toString())
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .email(email)
                .profile(profile)
                .provider(AuthProvider.valueOf(provider))
                .providerId(providerId)
                .role(Role.USER)
                .build();
    }
}
