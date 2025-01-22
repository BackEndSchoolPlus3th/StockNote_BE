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
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockTimeResponse {
    private String rt_cd;
    private String msg_cd;
    private String msg1;
    private Output1 output1;
    private List<Output2> output2;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output1 {
        private String stck_prpr;           // 주식 현재가
        private String prdy_vrss;           // 전일 대비
        private String prdy_vrss_sign;      // 전일 대비 부호
        private String prdy_ctrt;           // 전일 대비율
        private String acml_vol;            // 누적 거래량
        private String prdy_vol;            // 전일 거래량
        private String rprs_mrkt_kor_name;  // 대표 시장 한글명
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output2 {
        private String stck_cntg_hour;      // 주식 체결 시간
        private String stck_prpr;           // 주식 현재가
        private String prdy_vrss;           // 전일 대비
        private String prdy_vrss_sign;      // 전일 대비 부호
        private String prdy_ctrt;           // 전일 대비율
        private String askp;                // 매도호가
        private String bidp;                // 매수호가
        private String tday_rltv;           // 당일 체결강도
        private String acml_vol;            // 누적 거래량
        private String cnqn;                // 체결량
    }
}
