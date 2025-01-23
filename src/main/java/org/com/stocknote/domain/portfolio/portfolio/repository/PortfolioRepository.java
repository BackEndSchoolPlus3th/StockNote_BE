package org.com.stocknote.domain.portfolio.portfolio.repository;

import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
