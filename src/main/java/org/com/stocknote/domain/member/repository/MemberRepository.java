package org.com.stocknote.domain.member.repository;

import org.com.stocknote.domain.member.entity.AuthProvider;
import org.com.stocknote.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findById (Long memberId);

    Optional<Member> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
