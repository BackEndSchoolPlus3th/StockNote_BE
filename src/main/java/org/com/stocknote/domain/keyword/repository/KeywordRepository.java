package org.com.stocknote.domain.keyword.repository;

import org.com.stocknote.domain.keyword.entity.Keyword;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    void deleteByMemberId(Long id);

    List<org.com.stocknote.domain.keyword.entity.Keyword> findByMemberId(Long id);

    List<Keyword> findAllByPostCategory(PostCategory category);
}
