package org.com.stocknote.domain.member.dto;

import jdk.jshell.Snippet;
import lombok.*;
import org.com.stocknote.domain.member.entity.Member;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MemberDto {
    private String email;
    private String name;
    private String profile;

    public static MemberDto of (Member member) {
        return MemberDto.builder()
                .email(member.getEmail())
                .name(member.getName())
                .profile(member.getProfile())
                .build();
    }

}
