package org.com.stocknote.domain.portfolio.portfolioStock.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.portfolio.portfolio.service.PortfolioService;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.PfStockPatchRequest;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.PfStockRequest;
import org.com.stocknote.domain.portfolio.portfolioStock.entity.PfStock;
import org.com.stocknote.domain.portfolio.portfolioStock.repository.PfStockRepository;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stock.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PfStockService {
  private final PfStockRepository pfStockRepository;
  private final PortfolioService portfolioService;

  //임시
  private final StockRepository stockRepository;

  public List<PfStock> getStockList(Long portfolioNo) {
    return pfStockRepository.findByPortfolioId(portfolioNo);
  }

  public PfStock savePfStock(Long portfolioNo, PfStockRequest pfStockRequest) {
    Portfolio portfolio = portfolioService.getPortfolio(portfolioNo);
    Stock stock = stockRepository.findByCode(pfStockRequest.getStockCode()).orElse(null);
    PfStock pfStock = PfStock.builder()
        .portfolio(portfolio)
        .stock(stock)
        .pfstockCount(pfStockRequest.getPfstockCount())
        .pfstockPrice(pfStockRequest.getPfstockPrice())
        .pfstockTotalPrice(pfStockRequest.getPfstockPrice()*pfStockRequest.getPfstockCount())
        .build();
    return pfStockRepository.save(pfStock);
  }

  public PfStock savePfStock(PfStock pfStock) {
    return pfStockRepository.save(pfStock);
  }

  public List<Stock> getTempStockList() {
    return stockRepository.findAll();
  }

  public void deletePfStock(Long pfStockNo) {
    pfStockRepository.deleteById(pfStockNo);
  }

  public void update(Long pfStockNo, PfStockPatchRequest request) {
    PfStock pfStock = pfStockRepository.findById(pfStockNo).orElse(null);
    pfStock.setPfstockCount(request.getPfstockCount());
    pfStock.setPfstockPrice(request.getPfstockPrice());
    pfStock.setPfstockTotalPrice(
        request.getPfstockCount() * request.getPfstockPrice()
    );
    pfStockRepository.save(pfStock);
  }

  public Stock saveTempStock(Stock stock) {
    return stockRepository.save(stock);
  }

  public Stock getTempStock(String n) {
    return stockRepository.findById(n).orElse(null);
  }
}
