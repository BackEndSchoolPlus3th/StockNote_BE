package org.com.stocknote.domain.stock.stock_index.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

// merge 오류 해결용 주석

@Getter
@Setter
public class CurrentIndexResponseDto {
    @JsonProperty("rt_cd")
    private String rt_cd;

    @JsonProperty("msg_cd")
    private String msg_cd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("output")
    private CurrentIndexData output;

    @Getter
    @Setter
    public static class CurrentIndexData {
        @JsonProperty("bstp_nmix_prpr")
        private String bstp_nmix_prpr;

        @JsonProperty("bstp_nmix_prdy_vrss")
        private String bstp_nmix_prdy_vrss;

        @JsonProperty("prdy_vrss_sign")
        private String prdy_vrss_sign;

        @JsonProperty("bstp_nmix_prdy_ctrt")
        private String bstp_nmix_prdy_ctrt;

        @JsonProperty("acml_vol")
        private String acml_vol;

        @JsonProperty("prdy_vol")
        private String prdy_vol;

        @JsonProperty("acml_tr_pbmn")
        private String acml_tr_pbmn;

        @JsonProperty("prdy_tr_pbmn")
        private String prdy_tr_pbmn;

        @JsonProperty("bstp_nmix_oprc")
        private String bstp_nmix_oprc;

        @JsonProperty("prdy_nmix_vrss_nmix_oprc")
        private String prdy_nmix_vrss_nmix_oprc;

        @JsonProperty("oprc_vrss_prpr_sign")
        private String oprc_vrss_prpr_sign;

        @JsonProperty("bstp_nmix_oprc_prdy_ctrt")
        private String bstp_nmix_oprc_prdy_ctrt;

        @JsonProperty("bstp_nmix_hgpr")
        private String bstp_nmix_hgpr;

        @JsonProperty("prdy_nmix_vrss_nmix_hgpr")
        private String prdy_nmix_vrss_nmix_hgpr;

        @JsonProperty("hgpr_vrss_prpr_sign")
        private String hgpr_vrss_prpr_sign;

        @JsonProperty("bstp_nmix_hgpr_prdy_ctrt")
        private String bstp_nmix_hgpr_prdy_ctrt;

        @JsonProperty("bstp_nmix_lwpr")
        private String bstp_nmix_lwpr;

        @JsonProperty("prdy_clpr_vrss_lwpr")
        private String prdy_clpr_vrss_lwpr;

        @JsonProperty("lwpr_vrss_prpr_sign")
        private String lwpr_vrss_prpr_sign;

        @JsonProperty("prdy_clpr_vrss_lwpr_rate")
        private String prdy_clpr_vrss_lwpr_rate;

        @JsonProperty("ascn_issu_cnt")
        private String ascn_issu_cnt;

        @JsonProperty("uplm_issu_cnt")
        private String uplm_issu_cnt;

        @JsonProperty("stnr_issu_cnt")
        private String stnr_issu_cnt;

        @JsonProperty("down_issu_cnt")
        private String down_issu_cnt;

        @JsonProperty("lslm_issu_cnt")
        private String lslm_issu_cnt;

        @JsonProperty("dryy_bstp_nmix_hgpr")
        private String dryy_bstp_nmix_hgpr;

        @JsonProperty("dryy_hgpr_vrss_prpr_rate")
        private String dryy_hgpr_vrss_prpr_rate;

        @JsonProperty("dryy_bstp_nmix_hgpr_date")
        private String dryy_bstp_nmix_hgpr_date;

        @JsonProperty("dryy_bstp_nmix_lwpr")
        private String dryy_bstp_nmix_lwpr;

        @JsonProperty("dryy_lwpr_vrss_prpr_rate")
        private String dryy_lwpr_vrss_prpr_rate;

        @JsonProperty("dryy_bstp_nmix_lwpr_date")
        private String dryy_bstp_nmix_lwpr_date;

        @JsonProperty("total_askp_rsqn")
        private String total_askp_rsqn;

        @JsonProperty("total_bidp_rsqn")
        private String total_bidp_rsqn;

        @JsonProperty("seln_rsqn_rate")
        private String seln_rsqn_rate;

        @JsonProperty("shnu_rsqn_rate")
        private String shnu_rsqn_rate;

        @JsonProperty("ntby_rsqn")
        private String ntby_rsqn;
    }
}
