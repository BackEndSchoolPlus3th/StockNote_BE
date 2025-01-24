package org.com.stocknote.domain.portfolio.portfolio.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.portfolio.portfolio.dto.PortfolioPatchRequest;
import org.com.stocknote.domain.portfolio.portfolio.dto.PortfolioRequest;
import org.com.stocknote.domain.portfolio.portfolio.dto.PortfolioResponse;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.portfolio.portfolio.service.PortfolioService;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.PfStockResponse;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.StockTempResponse;
import org.com.stocknote.domain.portfolio.portfolioStock.entity.PfStock;
import org.com.stocknote.domain.portfolio.portfolioStock.service.PfStockService;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.global.dto.GlobalResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios")
public class PortfolioController {
  private final PortfolioService portfolioService;
  private final PfStockService pfStockService;

  @GetMapping
  public GlobalResponse<List<PortfolioResponse>> getPortfolioList() {
    List<Portfolio> portfolio = portfolioService.getPortfolioList();
    List<PortfolioResponse> response = portfolio.stream()
        .map(PortfolioResponse::from)
        .collect(Collectors.toList());
    return GlobalResponse.success(response);
  }

  @GetMapping("/{portfolio_no}")
  public GlobalResponse<List<PfStockResponse>> getPortfolioStock(
      @PathVariable("portfolio_no") Long portfolioNo) {
    List<PfStock> pfStockList = pfStockService.getStockList(portfolioNo);
    List<PfStockResponse> response = pfStockList.stream()
        .map(PfStockResponse::from)
        .collect(Collectors.toList());
    return GlobalResponse.success(response);
  }

  @PostMapping
  public GlobalResponse<String> addPortfolio(
      @RequestBody PortfolioRequest portfolioRequest) {
    portfolioService.save(portfolioRequest);
    return GlobalResponse.success("PortfolioList post");
  }

  @PatchMapping("/{portfolio_no}")
  public GlobalResponse<String> updatePortfolio(
      @PathVariable("portfolio_no") Long portfolioNo,
      @Valid @RequestBody PortfolioPatchRequest request) {
    portfolioService.update(portfolioNo, request);
    return GlobalResponse.success("Portfolio updated successfully");
  }

  @DeleteMapping("/{portfolio_no}")
  public GlobalResponse<String> deletePortfolio(
      @PathVariable("portfolio_no") Long portfolioNo
  ) {
    portfolioService.delete(portfolioNo);
    return GlobalResponse.success("Portfolio deleted successfully");
  }

  @GetMapping("/tempStock")
  public GlobalResponse<List<StockTempResponse>> getTempStockList() {
    List<Stock> stockList = pfStockService.getTempStockList();
    List<StockTempResponse> response = stockList.stream()
        .map(StockTempResponse::new)
        .collect(Collectors.toList());
    return GlobalResponse.success(response);
  }
}
