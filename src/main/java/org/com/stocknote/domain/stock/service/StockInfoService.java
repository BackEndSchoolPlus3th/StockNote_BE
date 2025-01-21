package org.com.stocknote.domain.stock.service;

import lombok.AllArgsConstructor;
import org.com.stocknote.domain.stock.dto.StockInfoResponse;
import org.com.stocknote.domain.stock.repository.StockInfoRepository;
import org.com.stocknote.global.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class StockInfoService {
    private final StockInfoRepository stockInfoRepository;

    //주식종목 검색
    public StockInfoResponse findStock(String name) {
        return stockInfoRepository.findByName(name)
                .map(StockInfoResponse::of)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.NOT_FOUND_STOCK + name));
    }
}
