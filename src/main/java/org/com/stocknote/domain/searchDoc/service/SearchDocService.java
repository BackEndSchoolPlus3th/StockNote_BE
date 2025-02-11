package org.com.stocknote.domain.searchDoc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.hashtag.entity.Hashtag;
import org.com.stocknote.domain.hashtag.repository.HashtagRepository;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.domain.post.dto.PostSearchConditionDto;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.com.stocknote.domain.searchDoc.document.*;
import org.com.stocknote.domain.searchDoc.repository.PortfolioDocRepository;
import org.com.stocknote.domain.searchDoc.repository.PortfolioStockDocRepository;
import org.com.stocknote.domain.searchDoc.repository.PostDocRepository;
import org.com.stocknote.domain.searchDoc.repository.StockDocRepository;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchDocService {
  private final StockDocRepository stockDocRepository;
  private final PortfolioDocRepository portfolioDocRepository;
  private final PortfolioStockDocRepository portfolioStockDocRepository;
  private final MemberRepository memberRepository;
  private final PostDocRepository postDocRepository;
  private final HashtagRepository hashtagRepository;

  public List<StockDoc> searchStocks(String keyword) {
    return stockDocRepository.searchByKeyword(keyword);
  }

  public PortfolioDoc getMyPortfolioList(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    PortfolioDoc portfolioDoc = portfolioDocRepository.findByMemberId(member.getId());

    return portfolioDoc;
  }

  public PostDoc savePostDoc(Post post) {
    List<Hashtag> hashtags = hashtagRepository.findByPostId(post.getId());

    List<String> hashtagList = hashtags.stream()
            .map(Hashtag::getName)
            .collect(Collectors.toList());

    PostDoc postDoc = PostDoc.builder()
            .id(post.getId().toString())
            .createdAt(post.getCreatedAt().toString())
            .modifiedAt(post.getModifiedAt().toString())
            .title(post.getTitle())
            .body(post.getBody())
            .category(post.getCategory())
            .hashtags(hashtagList) // 본문에서 해시태그 추출하는 메소드 필요
            .memberDoc(convertToMemberDoc(post.getMember())) // Member를 MemberDoc으로 변환하는 메소드 필요
            .build();

    return postDocRepository.save(postDoc);
  }


  private MemberDoc convertToMemberDoc(Member member) {
    return MemberDoc.of(member);
  }

  public List<PortfolioStockDoc> getMyPortfolioStockList(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    List<PortfolioStockDoc> portfolioStockDocList =
        portfolioStockDocRepository.findByMemberId(member.getId());
    return portfolioStockDocList;
  }

  public Page<PostDoc> searchPosts(PostSearchConditionDto condition, Pageable pageable) {
    if (!StringUtils.hasText(condition.getKeyword())) {
      return Page.empty(pageable);
    }

    String category = condition.getCategory();
    // ALL이거나 null인 경우는 카테고리 없이 검색
    if (category == null || PostCategory.ALL.name().equals(category)) {
      return switch (condition.getSearchType()) {
        case TITLE -> postDocRepository.searchByTitle(condition.getKeyword(), pageable);
        // logging: enable -> elastic search raw query 정상 1
        // spring application query를 날렸을 때는 검색 결과가 없다 2
        // 쿼리 1, 쿼리 2 비교
        // JPA -> query console
        case CONTENT -> postDocRepository.searchByContent(condition.getKeyword(), pageable);
        case USERNAME -> postDocRepository.searchByUsername(condition.getKeyword(), pageable);
        case HASHTAG -> postDocRepository.searchByHashtag(condition.getKeyword(), pageable);
        case ALL -> postDocRepository.searchByAll(condition.getKeyword(), pageable);
      };
    }

    // 카테고리가 있는 경우
    try {
      PostCategory.valueOf(category); // 유효한 카테고리인지 확인
      return switch (condition.getSearchType()) {
        case TITLE -> postDocRepository.searchByTitleAndCategory(condition.getKeyword(), category,
            pageable);
        case CONTENT -> postDocRepository.searchByContentAndCategory(condition.getKeyword(),
            category, pageable);
        case USERNAME -> postDocRepository.searchByUsernameAndCategory(condition.getKeyword(),
            category, pageable);
        case HASHTAG -> postDocRepository.searchByHashtagAndCategory(condition.getKeyword(),
            category, pageable);
        case ALL -> postDocRepository.searchByAllAndCategory(condition.getKeyword(), category,
            pageable);
      };
    } catch (IllegalArgumentException e) {
      log.error("Invalid category: {}", category);
      return Page.empty(pageable);
    }
  }
}
