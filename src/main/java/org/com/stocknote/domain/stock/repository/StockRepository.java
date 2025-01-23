package org.com.stocknote.domain.stock.repository;

import org.com.stocknote.domain.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, String> {
  Optional<Stock> findByCode(String stockCode);
}
