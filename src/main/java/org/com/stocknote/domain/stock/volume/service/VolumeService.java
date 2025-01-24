package org.com.stocknote.domain.stock.volume.service;

import org.com.stocknote.domain.stock.token.service.TokenService;
import org.com.stocknote.domain.stock.volume.dto.VolumeResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VolumeService {
    @Value("${kis.app-key}")
    private String appKey;
    @Value("${kis.app-secret}")
    private String appSecret;
    @Value("${kis.volume-base-url}")
    private String volumeBaseUrl;

    private final TokenService tokenService;
    private final WebClient.Builder webClientBuilder;

    public Mono<VolumeResponseDto> getVolumeData() {
        String accessToken = tokenService.getAccessToken();
        WebClient webClient = webClientBuilder.baseUrl(volumeBaseUrl).build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/volume-rank")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                        .queryParam("FID_COND_SCR_DIV_CODE", "20171")
                        .queryParam("FID_INPUT_ISCD", "0000")
                        .queryParam("FID_DIV_CLS_CODE", "0")
                        .queryParam("FID_BLNG_CLS_CODE", "0")
                        .queryParam("FID_TRGT_CLS_CODE", "111111111")
                        .queryParam("FID_TRGT_EXLS_CLS_CODE", "0000000000")
                        .queryParam("FID_INPUT_PRICE_1", "")
                        .queryParam("FID_INPUT_PRICE_2", "")
                        .queryParam("FID_VOL_CNT", "")
                        .queryParam("FID_INPUT_DATE_1", "")
                        .build())
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("tr_id", "FHPST01710000")
                .header("appkey", appKey)
                .header("appsecret", appSecret)
                .header("authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(VolumeResponseDto.class);
    }
}
