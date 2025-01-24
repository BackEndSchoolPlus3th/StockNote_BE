package org.com.stocknote.domain.member.dto;

import jdk.jshell.Snippet;
import lombok.*;

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
}