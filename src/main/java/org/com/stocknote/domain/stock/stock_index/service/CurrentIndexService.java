package org.com.stocknote.domain.stock.stock_index.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.stock.stock_index.dto.CurrentIndexResponseDto;
import org.com.stocknote.domain.stock.stock_index.dto.StockIndexDto;
import org.com.stocknote.domain.stock.token.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CurrentIndexService {
    @Value("${kis.app-key}")
    private String appKey;
    @Value("${kis.app-secret}")
    private String appSecret;
    @Value("${kis.index-base-url}")
    private String indexBaseUrl;

    private final TokenService tokenService;
    private final WebClient.Builder webClientBuilder;

    public Mono<CurrentIndexResponseDto> getKOSPI() {
        String accessToken = tokenService.getAccessToken();
        WebClient webClient = webClientBuilder.baseUrl(indexBaseUrl).build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/volume-rank")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "U")
                        .queryParam("FID_INPUT_ISCD", "0001")
                        .build())
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("tr_id", "FHPUP02100000")
                .header("custtype", "P")
                .header("appkey", appKey)
                .header("appsecret", appSecret)
                .header("authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(CurrentIndexResponseDto.class);
    }


    public Mono<CurrentIndexResponseDto> getKOSDAQ() {
        String accessToken = tokenService.getAccessToken();
        WebClient webClient = webClientBuilder.baseUrl(indexBaseUrl).build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/volume-rank")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "U")
                        .queryParam("FID_INPUT_ISCD", "1001")
                        .build())
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("tr_id", "FHPUP02100000")
                .header("custtype", "P")
                .header("appkey", appKey)
                .header("appsecret", appSecret)
                .header("authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(CurrentIndexResponseDto.class);
    }

    public Mono<CurrentIndexResponseDto> getKOSPI200() {
        String accessToken = tokenService.getAccessToken();
        WebClient webClient = webClientBuilder.baseUrl(indexBaseUrl).build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/volume-rank")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "U")
                        .queryParam("FID_INPUT_ISCD", "2001")
                        .build())
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("tr_id", "FHPUP02100000")
                .header("custtype", "P")
                .header("appkey", appKey)
                .header("appsecret", appSecret)
                .header("authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(CurrentIndexResponseDto.class);
    }


    // 필터링한 정보
    public Mono<StockIndexDto> getFilteredKOSPI() {
        return getKOSPI().map(this::KOSPIIntoStockIndexDto);
    }

    public Mono<StockIndexDto> getFilteredKOSDAQ() {
        return getKOSDAQ().map(this::KOSDAQIntoStockIndexDto);
    }

    public Mono<StockIndexDto> getFilteredKOSPI200() {
        return getKOSPI200().map(this::KOSPI200IntoStockIndexDto);
    }


    private StockIndexDto KOSPIIntoStockIndexDto(CurrentIndexResponseDto responseDto) {
        StockIndexDto dto = new StockIndexDto();
        CurrentIndexResponseDto.CurrentIndexData data = responseDto.getOutput();

        dto.setIndexName("코스피");
        dto.setCurrentValue(data.getBstp_nmix_prpr());  // 현재가
        dto.setChangeAmount(data.getBstp_nmix_prdy_vrss());  // 변화량
        dto.setChangeRate(data.getBstp_nmix_prdy_ctrt()); // 변화율
        dto.setChangeDirection(data.getPrdy_vrss_sign().equals("1") ? "▲" : "▼");  // 상승/하락 표시

        return dto;
    }

    private StockIndexDto KOSDAQIntoStockIndexDto(CurrentIndexResponseDto responseDto) {
        StockIndexDto dto = new StockIndexDto();
        CurrentIndexResponseDto.CurrentIndexData data = responseDto.getOutput();

        dto.setIndexName("코스닥");
        dto.setCurrentValue(data.getBstp_nmix_prpr());  // 현재가
        dto.setChangeAmount(data.getBstp_nmix_prdy_vrss());  // 변화량
        dto.setChangeRate(data.getBstp_nmix_prdy_ctrt()); // 변화율
        dto.setChangeDirection(data.getPrdy_vrss_sign().equals("1") ? "▲" : "▼");  // 상승/하락 표시

        return dto;
    }

    private StockIndexDto KOSPI200IntoStockIndexDto(CurrentIndexResponseDto responseDto) {
        StockIndexDto dto = new StockIndexDto();
        CurrentIndexResponseDto.CurrentIndexData data = responseDto.getOutput();

        dto.setIndexName("코스피200");
        dto.setCurrentValue(data.getBstp_nmix_prpr());  // 현재가
        dto.setChangeAmount(data.getBstp_nmix_prdy_vrss());  // 변화량
        dto.setChangeRate(data.getBstp_nmix_prdy_ctrt());  // 변화율
        dto.setChangeDirection(data.getPrdy_vrss_sign().equals("1") ? "▲" : "▼");  // 상승/하락 표시

        return dto;
    }
}
