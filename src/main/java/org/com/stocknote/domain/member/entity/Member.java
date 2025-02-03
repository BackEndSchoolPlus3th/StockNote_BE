package org.com.stocknote.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.global.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Member extends BaseEntity {
    private String name; // 이름
    private String account; // 아이디
    private String email; // 이메일
    private String profile; // 프로필 사진
    private String introduction; // 자기소개(한줄소개)

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;
    private String providerId;

    @OneToMany(mappedBy = "member")
    private List<Stock> stockList = new ArrayList<>();

    public void updateName(String newName) {
        this.name = newName;
    }

}