package org.com.stocknote.domain.portfolio.portfolio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.portfolio.portfolio.dto.PortfolioPatchRequest;
import org.com.stocknote.domain.portfolio.portfolio.dto.PortfolioRequest;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.portfolio.portfolio.repository.PortfolioRepository;
import org.com.stocknote.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final SecurityUtils securityUtils;


  public List<Portfolio> getPortfolioList() {
    Member member = securityUtils.getCurrentMember();
    return portfolioRepository.findByMember(member);
  }

  public Portfolio getPortfolio(Long portfolioNo) {
    return portfolioRepository.findById(portfolioNo)
        .orElseThrow(() -> new RuntimeException("Portfolio not found"));
  }

  @Transactional
  public void save(PortfolioRequest portfolioRequest) {
    Member member = securityUtils.getCurrentMember();
    Portfolio portfolio = Portfolio.builder()
        .name(portfolioRequest.getName())
        .description(portfolioRequest.getDescription())
        .member(member)
        .build();
    portfolioRepository.save(portfolio);
  }

  @Transactional
  public void update(Long portfoliNo, PortfolioPatchRequest portfolioPatchRequest) {
    Portfolio portfolio = portfolioRepository.findById(portfoliNo)
        .orElse(null);
    // 여기서 실패하면 프론트에 실패했다는 코드를 띄워줘야함
    if (portfolio == null) {
        log.error("Portfolio not found");
        return;
    }
    portfolio.setName(portfolioPatchRequest.getName().orElse(portfolio.getName()));
    portfolio.setDescription(portfolioPatchRequest.getDescription().orElse(portfolio.getDescription()));
  }

  @Transactional
  public void delete(Long portfolioNo) {
    portfolioRepository.deleteById(portfolioNo);
  }
}
