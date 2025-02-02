package org.com.stocknote.domain.portfolio.portfolioStock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.global.base.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PfStock extends BaseEntity {
  private int pfstockCount;
  private int pfstockPrice;
  private int pfstockTotalPrice;
  private int currentPrice;
  private String idxBztpSclsCdName; //종목소분류

  @Column(nullable = true)
  private LocalDateTime deletedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  private Portfolio portfolio;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stock_code") // stock_code를 외래 키로 사용
  private Stock stock;
}
