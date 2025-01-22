package org.com.stocknote.domain.stock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.stock.dto.StockDailyResponse;
import org.com.stocknote.domain.stock.dto.StockInfoResponse;
import org.com.stocknote.domain.stock.dto.StockPriceResponse;
import org.com.stocknote.domain.stock.dto.StockTimeResponse;
import org.com.stocknote.domain.stock.service.StockInfoService;
import org.com.stocknote.domain.stock.service.StockService;
import org.com.stocknote.global.dto.GlobalResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


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

    @GetMapping("/daily-prices")
    @Operation(summary = "일별 주식 데이터 조회")
    public StockDailyResponse getDailyStockPrices(
            @RequestParam String stockCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return stockService.getDailyStockPrices(stockCode, startDate, endDate);
    }

    @GetMapping("/weekly-prices")
    @Operation(summary = "주별 주식 데이터 조회")
    public StockDailyResponse getWeeklyStockPrices(
            @RequestParam String stockCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return stockService.getWeeklyStockPrices(stockCode, startDate, endDate);
    }

    @GetMapping("/monthly-prices")
    @Operation(summary = "월별 주식 데이터 조회")
    public StockDailyResponse getMonthlyStockPrices(
            @RequestParam String stockCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return stockService.getMonthlyStockPrices(stockCode, startDate, endDate);
    }

    @GetMapping("/yearly-prices")
    @Operation(summary = "연간 주식 데이터 조회")
    public StockDailyResponse getYearlyStockPrices(
            @RequestParam String stockCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return stockService.getYearlyStockPrices(stockCode, startDate, endDate);
    }

    @GetMapping("/time-prices")
    @Operation(summary = "당일 시간대별 체결 정보 조회")
    public StockTimeResponse getTimeStockPrices(@RequestParam String stockCode) {
        return stockService.getTimeStockPrices(stockCode);
    }

}
