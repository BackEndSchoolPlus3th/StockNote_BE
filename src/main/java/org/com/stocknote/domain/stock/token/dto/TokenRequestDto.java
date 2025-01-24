package org.com.stocknote.domain.stock.token.dto;

import lombok.Getter;
import lombok.Setter;

// merge 오류 해결용 주석

@Getter
@Setter
public class TokenRequestDto {
    private String grant_type;
    private String appkey;
    private String appsecret;

    public TokenRequestDto(String grant_type, String appKey, String appSecret) {
        this.grant_type = grant_type;
        this.appkey = appKey;
        this.appsecret = appSecret;
    }
}
