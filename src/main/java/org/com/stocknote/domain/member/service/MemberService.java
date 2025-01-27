package org.com.stocknote.domain.member.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.member.dto.MemberDto;
import org.com.stocknote.domain.member.dto.MemberRequest;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.entity.Role;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.com.stocknote.oauth.entity.OAuth2UserInfo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberDto findMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. 이메일: " + email));

        return MemberDto.builder()
                .email(member.getEmail())
                .name(member.getName())
                .profile(member.getProfile())
                .build();
    }
    @Transactional
    public MemberDto oauthSignup(OAuth2UserInfo userInfo) {
        // 이메일로 기존 회원 조회
        Member member = memberRepository.findByEmail(userInfo.email())
                .orElseGet(() -> userInfo.toEntity()); // 없으면 회원가입

        memberRepository.save(member); // 저장
        return MemberDto.of(member); // MemberDto로 변환 후 반환
    }

    @Transactional
    public MemberDto login(MemberRequest.LoginUserDto loginDto) {
        Member member = memberRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (member.getRole() == Role.USER && loginDto.getEmail() != null) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        return MemberDto.of(member);
    }

  public Member findByEmail(String userEmail) {
        return memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. 이메일: " + userEmail));
  }
}
