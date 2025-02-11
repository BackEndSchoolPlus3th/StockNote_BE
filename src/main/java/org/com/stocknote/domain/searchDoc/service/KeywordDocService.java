package org.com.stocknote.domain.searchDoc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.keyword.dto.KeywordRequest;
import org.com.stocknote.domain.searchDoc.document.KeywordDoc;
import org.com.stocknote.domain.searchDoc.repository.KeywordDocRepository;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import org.com.stocknote.domain.member.entity.Member;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordDocService {
    // @Query -> elasticsearchOperations
    private final ElasticsearchOperations elasticsearchOperations;
    private final KeywordDocRepository keywordDocRepository;

    public void save(Member member, KeywordRequest request) {
        List<KeywordDoc> keywordDocs = request.getKeywords().stream()
                .map(keywordDto -> KeywordDoc.builder()
                        .memberId(member.getId())
                        .keyword(keywordDto.getKeyword())
                        .postCategory(keywordDto.getPostCategory())
                        .build())
                .collect(Collectors.toList());

        try {
            elasticsearchOperations.save(keywordDocs);
        } catch (Exception e) {
            log.error("Failed to save keywords to Elasticsearch", e);
            // Optional: add error handling logic
        }
    }
}
