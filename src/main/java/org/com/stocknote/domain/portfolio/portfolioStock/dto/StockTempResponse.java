package org.com.stocknote.domain.portfolio.portfolioStock.dto;

import lombok.Data;
import org.com.stocknote.domain.stock.entity.Stock;

@Data
public class StockTempResponse {
  private String code; //종목코드
  private String name; //종목명
  private String category; //종목분류

  public StockTempResponse(Stock stock){
    this.code = stock.getCode();
    this.name = stock.getName();
    this.category = stock.getCategory();
  }
}
