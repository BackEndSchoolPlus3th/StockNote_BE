package org.com.stocknote.domain.stock.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stock.entity.StockInfo;

@Data
@Getter
@Builder
public class StockInfoResponse {
    private String name;
    private String code;

    public static StockInfoResponse of (StockInfo stockInfo){
        return StockInfoResponse.builder()
                .name(stockInfo.getName())
                .code(stockInfo.getCode())
                .build();
    }
}
