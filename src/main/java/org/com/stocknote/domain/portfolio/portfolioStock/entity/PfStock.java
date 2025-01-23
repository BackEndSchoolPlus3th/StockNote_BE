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

  @Column(nullable = true)
  private LocalDateTime deletedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  private Portfolio portfolio;

  @OneToOne
  private Stock stock;
}
