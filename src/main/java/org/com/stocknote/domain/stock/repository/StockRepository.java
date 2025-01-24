package org.com.stocknote.domain.stock.repository;

import org.com.stocknote.domain.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> {
}
