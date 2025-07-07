package org.com.stocknote.domain.post.repository;

import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.member m " +
            "LEFT JOIN FETCH p.comments c " +
            "WHERE p.category = :category " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.createdAt DESC")

    Page<Post> findByCategory(
            @Param("category") PostCategory category,
            Pageable pageable
    );

    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.member m " +
            "LEFT JOIN FETCH p.comments c " +
            "WHERE p.member = :member " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findByMember(
            @Param("member") Member member,
            Pageable pageable
    );

    //인기 순으로 정렬(댓글순 + 좋아요순 3일 이내)
    @Query(value = """
        SELECT p FROM Post p
        LEFT JOIN p.likes l
        LEFT JOIN p.comments c
        WHERE p.deletedAt IS NULL 
        AND p.createdAt >= :threeDaysAgo
        GROUP BY p
        ORDER BY COUNT(l) DESC, COUNT(c) DESC, p.createdAt DESC
    """,
            countQuery = """
        SELECT COUNT(p) FROM Post p
        WHERE p.deletedAt IS NULL 
        AND p.createdAt >= :threeDaysAgo
    """)
    Page<Post> findPopularPosts(@Param("threeDaysAgo") LocalDateTime threeDaysAgo, Pageable pageable);

    // 좋아요 순으로 정렬 (7일 이내)
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND p.createdAt >= :sevenDaysAgo ORDER BY size(p.likes) DESC")
    Page<Post> findPopularPostsByLikes(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo, Pageable pageable);

    // 댓글 순으로 정렬 (7일 이내)
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND p.createdAt >= :sevenDaysAgo ORDER BY size(p.comments) DESC")
    Page<Post> findPopularPostsByComments(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN Hashtag h ON h.postId = p.id " +
            "WHERE h.name = :sName " +
            "AND p.deletedAt IS NULL " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findByHashtagNameOrderByCreatedAtDesc(
            @Param("sName") String sName,
            Pageable pageable
    );
}
