package org.com.stocknote.domain.stock.stock_index.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockIndexDto {
    private String indexName; // 지수 이름
    private String currentValue; // 현재가(업종 지수 현재가): bstp_nmix_prpr
    private String changeAmount; // 변화량(업종 지수 전일 대비): bstp_nmix_prdy_vrss
    private String changeRate; // 변화율(업종 지수 전일 대비율): bstp_nmix_prdy_ctrt
    private String changeDirection; // 상승/하락 방향 (▲, ▼)
}
