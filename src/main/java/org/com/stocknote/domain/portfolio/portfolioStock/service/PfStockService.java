package org.com.stocknote.domain.portfolio.portfolioStock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.portfolio.note.repository.NoteRepository;
import org.com.stocknote.domain.portfolio.note.service.NoteService;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.portfolio.portfolio.repository.PortfolioRepository;
import org.com.stocknote.domain.portfolio.portfolio.service.PortfolioService;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.request.PfStockPatchRequest;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.request.PfStockRequest;
import org.com.stocknote.domain.portfolio.portfolioStock.entity.PfStock;
import org.com.stocknote.domain.portfolio.portfolioStock.repository.PfStockRepository;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stock.repository.StockRepository;
import org.com.stocknote.domain.stockApi.dto.response.StockPriceResponse;
import org.com.stocknote.domain.stockApi.service.StockApiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PfStockService {
  private final PfStockRepository pfStockRepository;
  private final NoteRepository noteRepository;
  private final PortfolioService portfolioService;
  private final NoteService noteService;
  private final StockApiService stockApiService;
  private final StockRepository stockRepository;
  private final PortfolioRepository portfolioRepository;

  @Transactional
  public PfStock savePfStock(Long portfolioNo, PfStockRequest pfStockRequest) {
    Portfolio portfolio = portfolioService.getPortfolio(portfolioNo);
    StockPriceResponse currentPrice = stockApiService.getStockPrice(pfStockRequest.getStockCode()).block();
    int currentPriceInt = Integer.parseInt(currentPrice.getOutput().getStck_prpr());

    Stock stock = stockRepository.findByCode(pfStockRequest.getStockCode()).orElse(null);
    String idxBztpSclsCdName = stockApiService.getStockInfo(pfStockRequest.getStockCode())
        .getOutput().getIdx_bztp_scls_cd_name();

    PfStock pfStock = PfStock.builder().portfolio(portfolio).stock(stock)
        .idxBztpSclsCdName(idxBztpSclsCdName)
        .pfstockCount(pfStockRequest.getPfstockCount())
        .pfstockPrice(pfStockRequest.getPfstockPrice())
        .pfstockTotalPrice(pfStockRequest.getPfstockPrice() * pfStockRequest.getPfstockCount())
        .currentPrice(currentPriceInt).build();

    return pfStockRepository.save(pfStock);
  }

  @Transactional
  public void buyPfStock(Long portfolioNo, Long pfStockNo,
                          PfStockPatchRequest pfStockPatchRequest) {
    Portfolio portfolio = portfolioService.getPortfolio(portfolioNo);
    PfStock pfStock = pfStockRepository.findById(pfStockNo).orElse(null);

    int quantity = pfStock.getPfstockCount();
    int totalPrice = pfStock.getPfstockPrice() * quantity;

    int buyQuantity = pfStockPatchRequest.getPfstockCount();
    int buyTotalPrice = pfStockPatchRequest.getPfstockPrice() * buyQuantity;

    quantity += buyQuantity;
    totalPrice += buyTotalPrice;

    int avgPrice = totalPrice / quantity;

    pfStock.setPfstockCount(quantity);
    pfStock.setPfstockTotalPrice(totalPrice);
    pfStock.setPfstockPrice(avgPrice);

    portfolio.setCash(portfolio.getCash() - buyTotalPrice);

    noteRepository.save(noteService.buyStock(portfolio, pfStock, pfStockPatchRequest));
    portfolioRepository.save(portfolio);
    pfStockRepository.save(pfStock);
  }

  @Transactional
  public void sellPfStock(Long portfolioNo, Long pfStockNo,
      PfStockPatchRequest pfStockPatchRequest) {
    try {
      Portfolio portfolio = portfolioService.getPortfolio(portfolioNo);
      PfStock pfStock = pfStockRepository.findById(pfStockNo)
          .orElseThrow(() -> new RuntimeException("Stock not found"));

      int quantity = pfStock.getPfstockCount();
      int sellQuantity = pfStockPatchRequest.getPfstockCount();
      int sellTotalPrice = pfStockPatchRequest.getPfstockPrice() * sellQuantity;

      // 매도 후 남은 수량 계산
      quantity -= sellQuantity;

      // 현금 업데이트
      portfolio.setCash(portfolio.getCash() + sellTotalPrice);

      if (quantity == 0) {
        // 수량이 0이면 Portfolio에서 PfStock 제거
        portfolio.getPfStockList().remove(pfStock);
        // DB에서 PfStock 삭제
        pfStockRepository.delete(pfStock);
        log.info("Stock with ID {} has been deleted", pfStockNo);
      } else {
        // 수량이 남아있으면 업데이트
        pfStock.setPfstockCount(quantity);
        int remainingTotalPrice = pfStock.getPfstockPrice() * quantity;
        pfStock.setPfstockTotalPrice(remainingTotalPrice);
        pfStockRepository.save(pfStock);
      }

      noteRepository.save(noteService.sellStock(portfolio, pfStock, pfStockPatchRequest));
      portfolioRepository.save(portfolio);

    } catch (Exception e) {
      log.error("Error in sellPfStock: ", e);
      throw new RuntimeException("Failed to process sell stock operation", e);
    }
  }

  public void update(Long portfolioNo, Long pfStockNo, PfStockPatchRequest pfStockPatchRequest) {
    Portfolio portfolio = portfolioService.getPortfolio(portfolioNo);

    PfStock pfStock = pfStockRepository.findById(pfStockNo).orElse(null);
    noteRepository.save(noteService.updateStock(portfolio, pfStock, pfStockPatchRequest));

    pfStock.setPfstockCount(pfStockPatchRequest.getPfstockCount());
    pfStock.setPfstockPrice(pfStockPatchRequest.getPfstockPrice());
    pfStock.setPfstockTotalPrice(pfStockPatchRequest.getPfstockCount() * pfStockPatchRequest.getPfstockPrice());

    pfStockRepository.save(pfStock);
  }


  @Transactional
  public void deletePfStock(Long portfolioNo, Long pfStockNo) {
    Portfolio portfolio = portfolioRepository.getReferenceById(portfolioNo);
    PfStock pfStock = pfStockRepository.getReferenceById(pfStockNo);

    noteRepository.save(noteService.deleteStock(portfolio, pfStock));
    pfStockRepository.deleteById(pfStockNo);
  }
}
