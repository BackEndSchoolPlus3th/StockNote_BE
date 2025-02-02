package org.com.stocknote.domain.stock.repository;

import org.com.stocknote.domain.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {
  Optional<Stock> findByCode(String stockCode);

  Optional<Stock> findByName(String stockName);

  List<Stock> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code);
}
