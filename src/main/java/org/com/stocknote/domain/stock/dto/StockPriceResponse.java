package org.com.stocknote.domain.stock.dto;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockPriceResponse {
    private String rt_cd;
    private String msg_cd;
    private String msg1;
    private Output output;

    @Data
    public static class Output {
        private String stck_prpr; // 현재가
        private String stck_oprc; // 시가
        private String stck_hgpr; // 고가
        private String stck_lwpr; // 저가
    }

    @Data
    public static class DailyData {
        private String stck_bsop_date; // 영업일자
        private String stck_clpr;      // 종가
        private String stck_oprc;      // 시가
        private String stck_hgpr;      // 고가
        private String stck_lwpr;      // 저가
        private String acml_vol;       // 누적 거래량
        private String acml_tr_pbmn;   // 누적 거래 대금
    }


}
