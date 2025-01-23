package org.com.stocknote.domain.portfolio.portfolio.dto;

import lombok.Builder;
import lombok.Getter;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.PfStockResponse;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PortfolioResponse {
  private Long id;
  private String category;
  private String name;
  private String description;
  private int totalAsset;
  private int cash;
  private int totalProfit;
  private int totalStock;
  private List<PfStockResponse> pfStocks;

  public static PortfolioResponse from(Portfolio portfolio) {
    return PortfolioResponse.builder()
        .id(portfolio.getId())
        .category(portfolio.getCategory())
        .name(portfolio.getName())
        .description(portfolio.getDescription())
        .totalAsset(portfolio.getTotalAsset())
        .cash(portfolio.getCash())
        .totalProfit(portfolio.getTotalProfit())
        .totalStock(portfolio.getTotalStock())
        .pfStocks(portfolio.getPfStockList().stream()
            .map(PfStockResponse::from)
            .collect(Collectors.toList()))
        .build();
  }
}
