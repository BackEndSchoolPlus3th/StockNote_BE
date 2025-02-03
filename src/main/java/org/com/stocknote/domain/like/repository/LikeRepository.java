package org.com.stocknote.domain.like.repository;

import org.com.stocknote.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByMemberIdAndPostId(Long memberId, Long postId);

    Long countByPostId(Long postId);

    void deleteByPostId(Long postId);

    Long postId(Long postId);
}
