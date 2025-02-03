package org.com.stocknote.domain.member.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.member.dto.ChangeNameRequest;
import org.com.stocknote.domain.member.dto.MemberDto;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.service.MemberService;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.com.stocknote.oauth.service.CustomOAuth2UserService;
import org.com.stocknote.oauth.token.TokenProvider;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = " 회원 API", description = "User")
public class MemberController {
    private final MemberService memberService;


    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Tag(name = " 회원정보 조회 API", description = "회원정보 가져옴.")
    public GlobalResponse<MemberDto> getUserProfile(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Member member = principalDetails.user();

        return GlobalResponse.success(MemberDto.of(member));
    }

    @PatchMapping("/profile/name")
    @PreAuthorize("isAuthenticated()")
    @Tag(name = " 회원 닉네임 변경 API", description = "닉네임 변경")
    public GlobalResponse<MemberDto> changeUserProfile(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody ChangeNameRequest request

    ) {
        System.out.println("request = " + request);
        Member member = principalDetails.user();
        Member updatedMember = memberService.updateProfile(member.getId(), request);

        return GlobalResponse.success(MemberDto.of(updatedMember));
    }
}
