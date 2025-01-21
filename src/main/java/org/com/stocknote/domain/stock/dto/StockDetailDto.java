package org.com.stocknote.domain.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockDetailDto {
    private String stockCode;
    private String stockName;
    private String currentPrice;
    private String volume;
}
