package org.com.stocknote.domain.comment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.global.base.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Comment extends BaseEntity {

    @Column(nullable = false)
    private Long postId;

    @Setter
    @Column(nullable = false)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    public Comment(Long postId, String body, Member member) {
        this.postId = postId;
        this.body = body;
        this.member = member;
    }
}
