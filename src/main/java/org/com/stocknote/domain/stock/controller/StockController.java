package org.com.stocknote.domain.stock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.stock.dto.StockInfoResponse;
import org.com.stocknote.domain.stock.dto.StockPriceResponse;
import org.com.stocknote.domain.stock.service.StockInfoService;
import org.com.stocknote.domain.stock.service.StockService;
import org.com.stocknote.global.dto.GlobalResponse;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stocks")
@Tag(name = "관심종목 API", description = "관심 종목(Stock)")
public class StockController {
    private final StockService stockService;
    private final StockInfoService stockInfoService;

    //이름으로 종목 검색
    @GetMapping
    @Operation(summary = "종목 이름 검색")
    public GlobalResponse findStock(@RequestParam String name) {
        StockInfoResponse stockInfoResponse = stockInfoService.findStock(name);
        return GlobalResponse.success(stockInfoResponse);
    }

    //종목 상세 검색 Api들

    @GetMapping("/price")
    public StockPriceResponse getStockPrice(@RequestParam String stockCode) {
        return stockService.getStockPrice(stockCode);
    }





}
