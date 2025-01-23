package org.com.stocknote.domain.portfolio.portfolioStock.dto;

import lombok.Builder;
import lombok.Getter;
import org.com.stocknote.domain.portfolio.portfolioStock.entity.PfStock;

@Getter
@Builder
public class PfStockResponse {
  private Long id;
  private int pfstockCount;
  private int pfstockPrice;
  private int pfstockTotalPrice;
  private String stockName;
  private String stockCode;

  public static PfStockResponse from(PfStock pfStock) {
    return PfStockResponse.builder()
        .id(pfStock.getId())
        .pfstockCount(pfStock.getPfstockCount())
        .pfstockPrice(pfStock.getPfstockPrice())
        .pfstockTotalPrice(pfStock.getPfstockTotalPrice())
        .stockName(pfStock.getStock().getName())
        .stockCode(pfStock.getStock().getCode())
        .build();
  }
}
