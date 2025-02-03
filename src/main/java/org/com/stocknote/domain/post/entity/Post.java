package org.com.stocknote.domain.post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.com.stocknote.domain.like.entity.Like;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.global.base.BaseEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "posts")
public class Post extends BaseEntity {
    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Like> likeList;

    @Enumerated(EnumType.STRING)
    private PostCategory category;

    private LocalDateTime deletedAt;

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void titleUpdate(String title) {
        this.title = title;
    }

    public void bodyUpdate(String body) {
        this.body = body;
    }
}