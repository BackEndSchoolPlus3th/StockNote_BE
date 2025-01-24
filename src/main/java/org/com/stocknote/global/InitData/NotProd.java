package org.com.stocknote.global.InitData;

import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.portfolio.portfolio.service.PortfolioService;
import org.com.stocknote.domain.portfolio.portfolioStock.entity.PfStock;
import org.com.stocknote.domain.portfolio.portfolioStock.service.PfStockService;
import org.com.stocknote.domain.stock.entity.Stock;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Configuration
@Profile("!prod")
@Slf4j  // 로그 추가
public class NotProd {
  @Bean
  public ApplicationRunner initNotProd(
      PfStockService pfStockService,
      PortfolioService portfolioService
  ) {
    return args -> {
      log.info("Initializing test data...");

      // 임시 주식데이터 생성
      List<Stock> stocks = new ArrayList<>();
      IntStream.rangeClosed(1,100).forEach(num -> {
        Stock stock = new Stock("code"+num, "종목"+num, "분류"+num/100);
        stocks.add(pfStockService.saveTempStock(stock));
      });
      log.info("Created {} stocks", stocks.size());

      // 임시 포트폴리오 데이터 생성
      AtomicInteger counter = new AtomicInteger(0);
      IntStream.rangeClosed(1,7).forEach(num -> {
        Portfolio portfolio = Portfolio.builder()
            .name("포트폴리오"+num)
            .category("카테고리"+num)
            .description("설명"+num)  // description 추가
            .totalAsset(num * 1000000)  // 초기값 설정
            .cash(num * 100000)         // 초기값 설정
            .totalProfit(num * 50000)   // 초기값 설정
            .totalStock(num * 10)       // 초기값 설정
            .build();

        List<PfStock> pfStockList = new ArrayList<>();
        IntStream.iterate(num, n -> n+10)
            .limit(10)
            .forEach(n -> {
              int index = counter.getAndIncrement() % stocks.size();
              Stock stock = stocks.get(index);
              PfStock pfStock = PfStock.builder()
                  .pfstockCount(n*10)
                  .pfstockPrice(n*100)
                  .pfstockTotalPrice(n*n*1000)
                  .stock(stock)
                  .build();
              pfStockList.add(pfStock);
              pfStock.setPortfolio(portfolio);  // 양방향 관계 설정
            });

        portfolio.setPfStockList(pfStockList);
        log.info("Saving portfolio {} with {} stocks", portfolio.getName(), pfStockList.size());
        portfolioService.savePfList(portfolio);
      });

      log.info("Test data initialization completed");
    };
  }
}
