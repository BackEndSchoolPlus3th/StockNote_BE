package org.com.stocknote.domain.stock.repository;

import org.com.stocknote.domain.stock.entity.StockInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockInfoRepository extends JpaRepository<StockInfo, Long> {
    Optional<StockInfo> findByName (String name);
}
