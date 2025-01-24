package org.com.stocknote.domain.stock.token.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseDto {
    @JsonProperty("access_token")
    private String access_token;
    @JsonProperty("token_type")
    private String token_type;
    @JsonProperty("expires_in")
    private int expires_in;
    @JsonProperty("access_token_token_expired")
    private String access_token_token_expired;

    public String getAccessToken() {
        return access_token;
    }
}
