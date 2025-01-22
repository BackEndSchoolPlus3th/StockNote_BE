package org.com.stocknote.domain.stock.service;


import lombok.AllArgsConstructor;
import org.com.stocknote.domain.stock.PeriodType;
import org.com.stocknote.domain.stock.dto.ChartResponse;
import org.com.stocknote.domain.stock.dto.StockDailyResponse;
import org.springframework.stereotype.Service;
import org.com.stocknote.domain.stock.dto.ChartResponse.CandleData;
import org.com.stocknote.domain.stock.dto.ChartResponse.StockSummary;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StockChartService {
    private final StockService stockService;

    public ChartResponse getChartData(String stockCode, PeriodType periodType, LocalDate startDate, LocalDate endDate) {
        // 기존 API 호출
        StockDailyResponse response = stockService.getStockPrices(stockCode, periodType, startDate, endDate);

        // 차트 데이터로 변환
        List<CandleData> candles = response.getOutput2().stream()
                .map(data -> CandleData.builder()
                        .time(data.getStck_bsop_date())
                        .open(Double.parseDouble(data.getStck_oprc()))
                        .high(Double.parseDouble(data.getStck_hgpr()))
                        .low(Double.parseDouble(data.getStck_lwpr()))
                        .close(Double.parseDouble(data.getStck_clpr()))
                        .volume(Long.parseLong(data.getAcml_vol()))
                        .value(Long.parseLong(data.getAcml_tr_pbmn()))
                        .build())
                .collect(Collectors.toList());

        // 요약 정보 생성
        StockSummary summary = StockSummary.builder()
                .currentPrice(Double.parseDouble(response.getOutput1().getStck_prpr()))
                .changePrice(Double.parseDouble(response.getOutput1().getPrdy_vrss()))
                .changeRate(Double.parseDouble(response.getOutput1().getPrdy_ctrt()))
                .volume(Long.parseLong(response.getOutput1().getAcml_vol()))
                .high(Double.parseDouble(response.getOutput1().getStck_hgpr()))
                .low(Double.parseDouble(response.getOutput1().getStck_lwpr()))
                .build();

        return ChartResponse.builder()
                .candles(candles)
                .summary(summary)
                .stockCode(stockCode)
                .stockName(response.getOutput1().getHts_kor_isnm())
                .build();
    }
}
