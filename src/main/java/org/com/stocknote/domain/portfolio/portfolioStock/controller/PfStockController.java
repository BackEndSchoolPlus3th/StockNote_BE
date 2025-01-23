package org.com.stocknote.domain.portfolio.portfolioStock.controller;


import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.PfStockRequest;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.StockTempResponse;
import org.com.stocknote.domain.portfolio.portfolioStock.service.PfStockService;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.global.dto.GlobalResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios/{portfolio_no}/stocks")
public class PfStockController {
  private final PfStockService pfStockService;

  @PostMapping
  public String postPortfolioCash() {
    return "Portfolio post";
  }

  @PostMapping("/AddStock")
  public GlobalResponse<String> addPortfolioStock(
      @PathVariable("portfolio_no") Long portfolioNo,
      @RequestBody PfStockRequest pfStockRequest) {

    pfStockService.savePfStock(portfolioNo, pfStockRequest);
    return GlobalResponse.success("Portfolio post");
  }

  @PatchMapping
  public String patchPortfolioStock() {
    return "Portfolio modify";
  }

  @DeleteMapping("/{pfStock_no}")
  public GlobalResponse<String> deletePortfolioStock(
      @PathVariable("portfolio_no") Long portfolioNo,
      @PathVariable("pfStock_no") Long pfStockNo) {

    pfStockService.deletePfStock(pfStockNo);
    return GlobalResponse.success("Portfolio del");
  }


}
