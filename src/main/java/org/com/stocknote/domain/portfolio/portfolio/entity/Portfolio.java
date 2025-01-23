package org.com.stocknote.domain.portfolio.portfolio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.com.stocknote.domain.portfolio.portfolioStock.entity.PfStock;
import org.com.stocknote.global.base.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Portfolio extends BaseEntity {
  private String category;
  private String name;
  private String description;
  private int totalAsset;
  private int cash;
  private int totalProfit;
  private int totalStock;

  @Column(nullable = true)
  private LocalDateTime deletedAt;

  @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
  private List<PfStock> pfStockList = new ArrayList<>();  // 초기화 추가

  // 연관관계 편의 메소드 추가
  public void addPfStock(PfStock pfStock) {
    this.pfStockList.add(pfStock);
    pfStock.setPortfolio(this);
  }
}
