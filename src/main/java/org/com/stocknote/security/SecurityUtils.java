package org.com.stocknote.security;

import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
  private final MemberRepository memberRepository;

  public SecurityUtils(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  // 현재 인증된 사용자의 이메일을 가져오는 메서드
  public String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }
    return authentication.getName();
  }

  // 현재 인증된 사용자의 Member 엔티티를 가져오는 메서드
  public Member getCurrentMember() {
    String email = getCurrentUserEmail();
    return memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
  }
}
