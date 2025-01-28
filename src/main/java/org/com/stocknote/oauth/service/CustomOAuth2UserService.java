package org.com.stocknote.oauth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.com.stocknote.oauth.entity.OAuth2UserInfo;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;


    @Transactional
    @Override
    public OAuth2User loadUser (OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 유저 정보(attributes) 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();
        // 2. resistrationId 가져오기 (third-party id)

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // 3. userNameAttributeName 가져오기
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // 4. 유저 정보 dto 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, oAuth2UserAttributes);

        // 5. 회원가입 및 로그인
        Member member = getOrSave(oAuth2UserInfo);
        System.out.println(member);
        // 6. OAuth2User로 반환
        return new PrincipalDetails(member, oAuth2UserAttributes, userNameAttributeName);
    }

    private Member getOrSave (OAuth2UserInfo oAuth2UserInfo) {
        Member member = memberRepository.findByEmail(oAuth2UserInfo.email())
                .orElseGet(oAuth2UserInfo::toEntity);
        return memberRepository.save(member);
    }

    @Transactional
    public OAuth2User processOAuth2User(String registrationId, String code) {
        // 1. ClientRegistration 동적으로 가져오기
        ClientRegistration clientRegistration = getClientRegistration(registrationId);

        // 2. Access Token 요청 및 발급
        OAuth2AccessToken accessToken = getAccessToken(clientRegistration, code);

        // 3. UserInfo 요청 및 처리
        Map<String, Object> attributes = getUserInfo2(clientRegistration, accessToken);

        // 4. OAuth2UserInfo 객체 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, attributes);

        // 5. 회원 저장 및 반환
        Member member = getOrSave(oAuth2UserInfo);
        System.out.println("member: " + member);
        return new PrincipalDetails(member, attributes, clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
    }

    private Map<String, Object> getUserInfo(ClientRegistration clientRegistration, OAuth2AccessToken accessToken) {
        // UserInfo Endpoint URI 가져오기
        String userInfoUri = clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri();

        // HTTP 요청 헤더 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getTokenValue()); // Access Token을 Authorization 헤더에 추가
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HTTP 요청 생성
        HttpEntity<String> request = new HttpEntity<>(headers);

        // RestTemplate 사용
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                request,
                Map.class
        );

        // 응답 본문 반환
        return response.getBody();
    }

    private OAuth2AccessToken getAccessToken(ClientRegistration clientRegistration, String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        System.out.println("clientRegistration.: " + clientRegistration);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientRegistration.getClientId());
        params.add("client_secret", clientRegistration.getClientSecret());
        params.add("code", code);
        params.add("redirect_uri", clientRegistration.getRedirectUri());


        System.out.println("redirect_uri: " + clientRegistration.getRedirectUri());

        // 디버깅을 위한 로그 추가
        log.info("Token request params: {}", params);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<Map> resp = rt.postForEntity(
                "https://oauth2.googleapis.com/token",
                new HttpEntity<>(params, headers),
                Map.class
        );
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Token request failed: " + resp);
        }
        Map body = resp.getBody();
        String tokenValue = (String) body.get("access_token");
        Number expiresIn = (Number) body.get("expires_in");
        return new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                tokenValue,
                Instant.now(),
                Instant.now().plusSeconds(expiresIn.longValue())
        );
    }

    public ClientRegistration getClientRegistration(String registrationId) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        System.out.println("re:"+registrationId);
        if (clientRegistration == null) {
            throw new IllegalArgumentException("Invalid registrationId: " + registrationId);
        }
        return clientRegistration;
    }
    private Map<String, Object> getUserInfo2(ClientRegistration cr, OAuth2AccessToken token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getTokenValue());

        RestTemplate rt = new RestTemplate();
        ResponseEntity<Map> resp = rt.exchange(
                cr.getProviderDetails().getUserInfoEndpoint().getUri(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );
        return resp.getBody();
    }
}
