package org.com.stocknote.domain.portfolio.portfolio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.portfolio.portfolio.dto.PortfolioPatchRequest;
import org.com.stocknote.domain.portfolio.portfolio.dto.PortfolioRequest;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.portfolio.portfolio.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;

  public void savePfList(Portfolio portfolio) {
    portfolioRepository.save(portfolio);

  }

  public List<Portfolio> getPortfolioList() {
    return portfolioRepository.findAll();
  }

  public void save(PortfolioRequest portfolioRequest) {
    Portfolio portfolio = Portfolio.builder()
        .name(portfolioRequest.getName())
        .description(portfolioRequest.getDescription())
        .build();
    savePfList(portfolio);
  }

  public void update(PortfolioPatchRequest portfolioPatchRequest) {
    Portfolio portfolio = portfolioRepository.findById(portfolioPatchRequest.getId())
        .orElse(null);
    // 여기서 실패하면 프론트에 실패했다는 코드를 띄워줘야함
    if (portfolio == null) {
        log.error("Portfolio not found");
        return;
    }

    portfolio.setName(portfolioPatchRequest.getName().orElse(portfolio.getName()));
    portfolio.setDescription(portfolioPatchRequest.getDescription().orElse(portfolio.getDescription()));

    portfolioRepository.save(portfolio);
  }

  public Portfolio getPortfolio(Long portfolioNo) {
    return portfolioRepository.findById(portfolioNo)
        .orElseThrow(() -> new RuntimeException("Portfolio not found"));
  }

  public void delete(Long portfolioNo) {
    portfolioRepository.deleteById(portfolioNo);
  }
}
