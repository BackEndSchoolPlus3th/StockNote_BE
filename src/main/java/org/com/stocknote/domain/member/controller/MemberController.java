package org.com.stocknote.domain.member.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.member.dto.MemberDto;
import org.com.stocknote.domain.member.service.MemberService;
import org.com.stocknote.oauth.service.CustomOAuth2UserService;
import org.com.stocknote.oauth.token.TokenProvider;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = " 회원 API", description = "User")
public class MemberController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final CustomOAuth2UserService oAuth2UserService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public GlobalResponse<MemberDto> getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        MemberDto userProfile = memberService.findMemberByEmail(email);

        return GlobalResponse.success(userProfile);
    }
}
