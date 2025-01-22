package org.com.stocknote.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // 알 수 없는 필드 무시
public class StockDailyResponse {
    private String rt_cd;
    private String msg_cd;
    private String msg1;
    private Output1 output1;
    private List<Output2> output2;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output1 {
        private String prdy_vrss;           // 전일 대비
        private String prdy_vrss_sign;      // 전일 대비 부호
        private String prdy_ctrt;           // 전일 대비율
        private String stck_prpr;           // 주식 현재가
        private String stck_oprc;           // 시가
        private String stck_hgpr;           // 최고가
        private String stck_lwpr;           // 저가
        private String acml_vol;            // 누적 거래량
        private String acml_tr_pbmn;        // 누적 거래 대금
        private String hts_kor_isnm;        // 종목명
        private String stck_prdy_clpr;      // 주식 전일 종가
        private String stck_mxpr;           // 상한가
        private String stck_llam;           // 하한가
        private String stck_prdy_oprc;      // 주식 전일 시가
        private String stck_prdy_hgpr;      // 주식 전일 최고가
        private String stck_prdy_lwpr;      // 주식 전일 최저가
        private String askp;                // 매도호가
        private String bidp;                // 매수호가
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output2 {
        private String stck_bsop_date;      // 주식 영업 일자
        private String stck_clpr;           // 주식 종가
        private String stck_oprc;           // 주식 시가
        private String stck_hgpr;           // 주식 최고가
        private String stck_lwpr;           // 주식 최저가
        private String acml_vol;            // 누적 거래량
        private String acml_tr_pbmn;        // 누적 거래 대금
        private String prdy_vrss;           // 전일 대비
        private String prdy_vrss_sign;      // 전일 대비 부호
        private String flng_cls_code;       // 락구분 코드
        private String prtt_rate;           // 분할 비율
        private String mod_yn;              // 분할변경여부
    }
}
