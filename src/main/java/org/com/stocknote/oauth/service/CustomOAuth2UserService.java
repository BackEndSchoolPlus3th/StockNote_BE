package org.com.stocknote.oauth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.member.entity.AuthProvider;
import org.com.stocknote.domain.member.entity.Role;
import org.com.stocknote.oauth.entity.OAuth2UserInfo;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;


    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, oAuth2User.getAttributes());
        String providerId = oAuth2UserInfo.providerId();

        Member member = getOrCreateMember(provider, providerId, oAuth2UserInfo);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("memberId", member.getId());

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        return new PrincipalDetails(member, attributes, userNameAttributeName);
    }

    @Transactional
    private Member getOrCreateMember(AuthProvider provider, String providerId, OAuth2UserInfo userInfo) {

        return memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    Optional<Member> existingMember = memberRepository.findByEmail(userInfo.email());

                    if (existingMember.isPresent()) {
                        throw new OAuth2AuthenticationException(
                                "Email already exists with different provider: " + userInfo.email());
                    }

                    Member newMember = Member.builder()
                            .email(userInfo.email())
                            .name(userInfo.name())
                            .profile(userInfo.profile())
                            .provider(provider)
                            .providerId(providerId)
                            .role(Role.USER)
                            .build();

                    return memberRepository.save(newMember);
                });
    }


}
