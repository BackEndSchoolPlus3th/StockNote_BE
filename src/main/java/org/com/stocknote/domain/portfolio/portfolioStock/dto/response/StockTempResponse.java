package org.com.stocknote.domain.portfolio.portfolioStock.dto.response;

import lombok.Data;
import org.com.stocknote.domain.stock.entity.Stock;

@Data
public class StockTempResponse {
  private String code; //종목코드
  private String name; //종목명
  private String market; //시장구분

  public StockTempResponse(Stock stock){
    this.code = stock.getCode();
    this.name = stock.getName();
    this.market = stock.getMarket();
  }
}
