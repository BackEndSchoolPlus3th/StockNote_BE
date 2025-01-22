package org.com.stocknote.domain.stock.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ChartResponse {
    private List<CandleData> candles;
    private StockSummary summary;
    private String stockCode;
    private String stockName;

    @Getter
    @Builder
    public static class CandleData {
        private String time;      // 시간
        private double open;      // 시가
        private double high;      // 고가
        private double low;       // 저가
        private double close;     // 종가
        private long volume;      // 거래량
        private long value;       // 거래대금
    }

    @Getter
    @Builder
    public static class StockSummary {
        private double currentPrice;  // 현재가
        private double changePrice;   // 변동금액
        private double changeRate;    // 변동률
        private long volume;          // 거래량
        private double high;          // 고가
        private double low;           // 저가
    }
}
