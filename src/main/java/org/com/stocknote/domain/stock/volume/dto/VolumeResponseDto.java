package org.com.stocknote.domain.stock.volume.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


// merge 오류 해결용 주석

@Getter
@Setter
//@JsonIgnoreProperties(ignoreUnknown = true) => JSON 응답에 클래스에 정의되지 않은 필드가 있어도 무시하고 매핑을 진행
public class VolumeResponseDto {
    @JsonProperty("rt_cd")
    private String rtCd;

    @JsonProperty("msg_cd")
    private String msgCd;

    @JsonProperty("msg1")
    private String msg1;

    @JsonProperty("output")
    private List<VolumeData> output;

    @Getter
    @Setter
    public static class VolumeData {
        @JsonProperty("hts_kor_isnm")
        private String htsKorIsnm;

        @JsonProperty("mksc_shrn_iscd")
        private String mkscShrnIscd;

        @JsonProperty("data_rank")
        private String dataRank;

        @JsonProperty("stck_prpr")
        private String stckPrpr;

        @JsonProperty("prdy_vrss_sign")
        private String prdyVrssSign;

        @JsonProperty("prdy_vrss")
        private String prdyVrss;

        @JsonProperty("prdy_ctrt")
        private String prdyCtrt;

        @JsonProperty("acml_vol")
        private String acmlVol;

        @JsonProperty("prdy_vol")
        private String prdyVol;

        @JsonProperty("lstn_stcn")
        private String lstnStcn;

        @JsonProperty("avrg_vol")
        private String avrgVol;

        @JsonProperty("n_befr_clpr_vrss_prpr_rate")
        private String nBefrClprVrssPrprRate;

        @JsonProperty("vol_inrt")
        private String volInrt;

        @JsonProperty("vol_tnrt")
        private String volTnrt;

        @JsonProperty("avrg_tr_pbmn")
        private String avrgTrPbmn;

        @JsonProperty("tr_pbmn_tnrt")
        private String trPbmnTnrt;

        @JsonProperty("nday_tr_pbmn_tnrt")
        private String ndayTrPbmnTnrt;

        @JsonProperty("nday_vol_tnrt")
        private String ndayVolTnrt;

        @JsonProperty("acml_tr_pbmn")
        private String acmlTrPbmn;
    }
}