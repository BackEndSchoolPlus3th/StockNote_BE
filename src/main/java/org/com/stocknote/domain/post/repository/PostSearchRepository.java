package org.com.stocknote.domain.post.repository;

import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.dto.PostSearchConditionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@Repository
public interface PostSearchRepository {
    Page<Post> search(PostSearchConditionDto condition, Pageable pageable);


}
