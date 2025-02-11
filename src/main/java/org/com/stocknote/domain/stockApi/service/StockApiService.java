package org.com.stocknote.domain.stockApi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.stockApi.dto.response.SectorResponse;
import org.com.stocknote.domain.stock.repository.StockRepository;
import org.com.stocknote.domain.stockApi.dto.*;
import org.com.stocknote.domain.stockApi.type.PeriodType;
import org.com.stocknote.domain.stockApi.dto.response.*;
import org.com.stocknote.domain.stockApi.stockToken.service.StockTokenService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
// 받아온 정보들을 우리 서비스에 맞게끔 데이터를 parsing 하고, cleaning해서 내려주는 역할
public class StockApiService {

    // webClientBuilder
    private final WebClient.Builder webClientBuilder;
    private final WebClient webClient;
    private final NaverApiClient naverApiClient;
    private final StockTokenService stockTokenService;
    private final StockRepository stockRepository;
    private final ObjectMapper objectMapper;

    //임시 추가
    private final RestTemplate restTemplate;

    // getKospi -> /uapi/domestic-stock/v1/quotations/volume-rank api call
    public Mono<CurrentIndexResponse> getKOSPI() {
        String accessToken = stockTokenService.getAccessToken();
//        WebClient webClient = webClientBuilder.baseUrl(stockTokenService.getIndexBaseUrl()).build();

        // service를 분리시키는 것
        // service 내에서 header, bodytoMono 이런 Template들을 단순화 시키는 것(library를 통해서)
        return naverApiClient.getKospi();
    }

    public Mono<CurrentIndexResponse> getKOSDAQ() {
        String accessToken = stockTokenService.getAccessToken();
        WebClient webClient = webClientBuilder.baseUrl(stockTokenService.getIndexBaseUrl()).build();

        // template 코드
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/volume-rank")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "U")
                        .queryParam("FID_INPUT_ISCD", "1001")
                        .build())
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("tr_id", "FHPUP02100000")
                .header("custtype", "P")
                .header("appkey", stockTokenService.getAppKey())
                .header("appsecret", stockTokenService.getAppSecret())
                .header("authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(CurrentIndexResponse.class);
    }

    public Mono<CurrentIndexResponse> getKOSPI200() {
        String accessToken = stockTokenService.getAccessToken();
        WebClient webClient = webClientBuilder.baseUrl(stockTokenService.getIndexBaseUrl()).build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/volume-rank")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "U")
                        .queryParam("FID_INPUT_ISCD", "2001")
                        .build())
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("tr_id", "FHPUP02100000")
                .header("custtype", "P")
                .header("appkey", stockTokenService.getAppKey())
                .header("appsecret", stockTokenService.getAppSecret())
                .header("authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(CurrentIndexResponse.class);
    }

    public Mono<VolumeResponse> getVolumeData() {
        String accessToken = stockTokenService.getAccessToken();
        WebClient webClient = webClientBuilder.baseUrl(stockTokenService.getVolumeBaseUrl()).build();

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
                .header("appkey", stockTokenService.getAppKey())
                .header("appsecret", stockTokenService.getAppSecret())
                .header("authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(VolumeResponse.class);
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


    private StockIndexDto KOSPIIntoStockIndexDto(CurrentIndexResponse responseDto) {
        StockIndexDto dto = new StockIndexDto();
        CurrentIndexResponse.CurrentIndexData data = responseDto.getOutput();

        dto.setIndexName("코스피");
        dto.setCurrentValue(data.getBstp_nmix_prpr());  // 현재가
        dto.setChangeAmount(data.getBstp_nmix_prdy_vrss());  // 변화량
        dto.setChangeRate(data.getBstp_nmix_prdy_ctrt()); // 변화율
        dto.setChangeDirection(data.getPrdy_vrss_sign().equals("1") ? "▲" : "▼");  // 상승/하락 표시

        return dto;
    }

    private StockIndexDto KOSDAQIntoStockIndexDto(CurrentIndexResponse responseDto) {
        StockIndexDto dto = new StockIndexDto();
        CurrentIndexResponse.CurrentIndexData data = responseDto.getOutput();

        dto.setIndexName("코스닥");
        dto.setCurrentValue(data.getBstp_nmix_prpr());  // 현재가
        dto.setChangeAmount(data.getBstp_nmix_prdy_vrss());  // 변화량
        dto.setChangeRate(data.getBstp_nmix_prdy_ctrt()); // 변화율
        dto.setChangeDirection(data.getPrdy_vrss_sign().equals("1") ? "▲" : "▼");  // 상승/하락 표시

        return dto;
    }

    private StockIndexDto KOSPI200IntoStockIndexDto(CurrentIndexResponse responseDto) {
        StockIndexDto dto = new StockIndexDto();
        CurrentIndexResponse.CurrentIndexData data = responseDto.getOutput();

        dto.setIndexName("코스피200");
        dto.setCurrentValue(data.getBstp_nmix_prpr());  // 현재가
        dto.setChangeAmount(data.getBstp_nmix_prdy_vrss());  // 변화량
        dto.setChangeRate(data.getBstp_nmix_prdy_ctrt());  // 변화율
        dto.setChangeDirection(data.getPrdy_vrss_sign().equals("1") ? "▲" : "▼");  // 상승/하락 표시

        return dto;
    }

    //실시간 가격 조회
    public Mono<StockPriceResponse> getStockPrice(String stockCode) {
        return webClientBuilder
                .baseUrl(stockTokenService.getIndexBaseUrl())
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/inquire-price")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                        .queryParam("FID_INPUT_ISCD", stockCode)
                        .build())
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.set("authorization", "Bearer " + stockTokenService.getAccessToken());
                    headers.set("appkey", stockTokenService.getAppKey());
                    headers.set("appsecret", stockTokenService.getAppSecret());
                    headers.set("tr_id", "FHKST01010100");
                })
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        return objectMapper.readValue(response, StockPriceResponse.class);
                    } catch (Exception e) {
                        System.err.println("❌ JSON 매핑 오류: " + e.getMessage());
                        return null;
                    }
                })
                .onErrorMap(WebClientResponseException.class, e -> {
                    return new RuntimeException("주식 가격 조회 실패: " + e.getStatusCode(), e);
                })
                .onErrorMap(Exception.class, e -> {
                    return new RuntimeException("주식 가격 조회 중 오류 발생", e);
                });
    }


    // 날자별 가격 조회
    public StockDailyResponse getStockPrices(String stockCode, PeriodType periodType, LocalDate startDate, LocalDate endDate) {
        String baseUrl = "https://openapivts.koreainvestment.com:29443";
        String endpoint = "/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice";

        // 날짜 포맷 변환
        String formattedStartDate = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String formattedEndDate = endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        try {
            String response = webClientBuilder
                    .baseUrl(baseUrl)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(endpoint)
                            .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                            .queryParam("FID_INPUT_ISCD", stockCode)
                            .queryParam("FID_INPUT_DATE_1", formattedStartDate)
                            .queryParam("FID_INPUT_DATE_2", formattedEndDate)
                            .queryParam("FID_PERIOD_DIV_CODE", periodType.getCode())
                            .queryParam("FID_ORG_ADJ_PRC", "0")
                            .build())
                    .headers(headers -> {
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.set("authorization", "Bearer " + stockTokenService.getAccessToken());
                        headers.set("appkey", stockTokenService.getAppKey());
                        headers.set("appsecret", stockTokenService.getAppSecret());
                        headers.set("tr_id", "FHKST03010100");
                    })
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        return Mono.error(new RuntimeException("주식 데이터 조회 실패: " + errorBody));
                                    })
                    )
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readValue(response, StockDailyResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("주식 데이터 조회 중 오류 발생", e);
        }
    }

    public StockTimeResponse getTimeStockPrices(String stockCode) {
        String baseUrl = "https://openapivts.koreainvestment.com:29443";
        String endpoint = "/uapi/domestic-stock/v1/quotations/inquire-time-itemconclusion";

        try {
            String stockName = stockRepository.findByCode(stockCode).get().getName();
            LocalDate today = LocalDate.now();
            LocalDate lastTradingDay = getLastTradingDay(today);
            LocalDate finalLastTradingDay = lastTradingDay;
            String response = webClientBuilder
                    .baseUrl(baseUrl)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(endpoint)
                            .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                            .queryParam("FID_INPUT_ISCD", stockCode)
                            .queryParam("FID_INPUT_DATE_1", finalLastTradingDay.format(DateTimeFormatter.BASIC_ISO_DATE))
                            .queryParam("FID_INPUT_HOUR_1", "000000")
                            .build())
                    .headers(headers -> {
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.set("authorization", "Bearer " + stockTokenService.getAccessToken());
                        headers.set("appkey", stockTokenService.getAppKey());
                        headers.set("appsecret", stockTokenService.getAppSecret());
                        headers.set("tr_id", "FHPST01060000");
                    })
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Client error for stockCode {}: {}", stockCode, errorBody);
                                        return Mono.error(new RuntimeException("시간대별 주식 데이터 조회 실패: " + errorBody));
                                    })
                    )
                    .bodyToMono(String.class)
                    .block();

            log.debug("API Response: {}", response);

            StockTimeResponse timeResponse = objectMapper.readValue(response, StockTimeResponse.class);
            if (timeResponse == null || timeResponse.getOutput2() == null || timeResponse.getOutput2().isEmpty()) {
                lastTradingDay = lastTradingDay.minusDays(1);
                log.warn("No time data returned for stockCode: {}", stockCode);
            }
            if (timeResponse != null) {
                timeResponse.setStockName(stockName);
            }
            return timeResponse;

        } catch (JsonProcessingException e) {
            log.error("JSON parsing error for stockCode {}: {}", stockCode, e.getMessage());
            throw new RuntimeException("시간대별 주식 데이터 파싱 실패", e);
        } catch (Exception e) {
            log.error("API Error for stockCode {}: {}", stockCode, e.getMessage());
            throw new RuntimeException("시간대별 주식 데이터 조회 중 오류 발생", e);
        }
    }

    public ChartResponse getChartData(String stockCode, PeriodType periodType, LocalDate startDate, LocalDate endDate) {
        if (periodType == PeriodType.TIME) {
            String stockName = stockRepository.findByCode(stockCode).get().getName();
            if (stockName == null) stockName = "알 수 없음"; // 기본값 처리
            // ✅ 시간별 체결 정보 조회 (getTimeStockPrices)
            StockTimeResponse timeResponse = getTimeStockPrices(stockCode);
            if (timeResponse == null || timeResponse.getOutput2() == null || timeResponse.getOutput2().isEmpty()) {
                throw new RuntimeException("시간대별 주식 데이터가 없습니다.");
            }

            List<ChartResponse.CandleData> candles = new ArrayList<>();
            for (StockTimeResponse.Output2 node : timeResponse.getOutput2()) {
                candles.add(ChartResponse.CandleData.builder()
                        .time(node.getStck_cntg_hour())
                        .open(Double.parseDouble(node.getStck_prpr())) // 현재가를 open으로 설정
                        .high(Double.parseDouble(node.getStck_prpr())) // 체결 단위에서는 high/low가 동일
                        .low(Double.parseDouble(node.getStck_prpr()))
                        .close(Double.parseDouble(node.getStck_prpr()))
                        .volume(Long.parseLong(node.getAcml_vol()))
                        .value(Long.parseLong(node.getCnqn())) // 체결량 사용
                        .build());
            }

            return ChartResponse.builder()
                    .stockCode(stockCode)
                    .stockName(stockName)
                    .summary(ChartResponse.StockSummary.builder()
                            .changePrice(Double.parseDouble(timeResponse.getOutput1().getPrdy_vrss()))
                            .changeRate(Double.parseDouble(timeResponse.getOutput1().getPrdy_ctrt()))
                            .volume(Long.parseLong(timeResponse.getOutput1().getAcml_vol()))
                            .build())
                    .candles(candles)
                    .build();
        }
        WebClient webClient = webClientBuilder.baseUrl(stockTokenService.getIndexBaseUrl()).build();
        String endpoint = "/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice";

        try {
            String finalEndpoint = endpoint;
            String response = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(finalEndpoint)
                            .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                            .queryParam("FID_INPUT_ISCD", stockCode)
                            .queryParam("FID_INPUT_DATE_1", startDate.toString())
                            .queryParam("FID_INPUT_DATE_2", endDate.toString())
                            .queryParam("FID_PERIOD_DIV_CODE", periodType.getCode())
                            .queryParam("FID_ORG_ADJ_PRC", "0")
                            .build())
                    .headers(headers -> {
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.set("authorization", "Bearer " + stockTokenService.getAccessToken());
                        headers.set("appkey", stockTokenService.getAppKey());
                        headers.set("appsecret", stockTokenService.getAppSecret());
                        headers.set("tr_id", "FHKST03010100");
                    })
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // ✅ JSON을 직접 파싱하여 output1, output2 처리
            JsonNode root = objectMapper.readTree(response);
            JsonNode output1 = root.path("output1");
            JsonNode output2 = root.path("output2");

            // ✅ StockSummary 생성 (output1 기반)
            ChartResponse.StockSummary summary = ChartResponse.StockSummary.builder()
                    .changePrice(output1.path("prdy_vrss").asDouble())
                    .changeRate(output1.path("prdy_ctrt").asDouble())
                    .volume(output1.path("acml_vol").asLong())
                    .build();

            // ✅ CandleData 리스트 생성 (output2 기반)
            List<ChartResponse.CandleData> candles = new ArrayList<>();
            for (JsonNode node : output2) {
                candles.add(ChartResponse.CandleData.builder()
                        .time(node.path("stck_bsop_date").asText()) // 거래일자
                        .open(node.path("stck_oprc").asDouble())
                        .high(node.path("stck_hgpr").asDouble())
                        .low(node.path("stck_lwpr").asDouble())
                        .close(node.path("stck_clpr").asDouble())
                        .volume(node.path("acml_vol").asLong())
                        .value(node.path("acml_tr_pbmn").asLong())
                        .build());
            }

            // ✅ 최종 ChartResponse 객체 생성
            return ChartResponse.builder()
                    .stockCode(output1.path("stck_shrn_iscd").asText())  // 종목코드
                    .stockName(output1.path("hts_kor_isnm").asText())    // 종목명
                    .summary(summary)
                    .candles(candles)
                    .build();

        } catch (Exception e) {
            log.error("❌ 차트 데이터 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("차트 데이터 조회 중 오류 발생", e);
        }
    }
    private LocalDate getLastTradingDay(LocalDate date) {
        // 주말인 경우 금요일로 설정
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return date.minusDays(1);
        } else if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return date.minusDays(2);
        }
        // TODO: 공휴일 체크 로직 추가
        return date;
    }

    public SectorResponse getStockInfo(String pdno) {
        log.info("Getting stock info for PDNO: {}", pdno);
        String baseUrl = "https://openapi.koreainvestment.com:9443";
        String endpoint = "/uapi/domestic-stock/v1/quotations/search-stock-info";

        // Build URL with query parameters
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + endpoint)
            .queryParam("PDNO", pdno)
            .queryParam("PRDT_TYPE_CD", "300")
            .toUriString();

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + stockTokenService.getAccessToken());
        headers.set("appkey", stockTokenService.getAppKey());
        headers.set("appsecret", stockTokenService.getAppSecret());
        headers.set("tr_id", "CTPF1002R");
        headers.set("custtype", "P");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            log.info("Requesting stock info for PDNO: {}", pdno);
            ResponseEntity<SectorResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                SectorResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                log.debug("Successfully retrieved stock info for PDNO: {}", pdno);
                log.debug("Stock info: {}", response.getBody().toString());
                return response.getBody();
            } else {
                log.error("Failed to get stock info. Status code: {}", response.getStatusCode());
                throw new RuntimeException("Failed to get stock info. Status code: " + response.getStatusCode());
            }
        } catch (HttpStatusCodeException e) {
            log.error("Stock info lookup failed: {}", e.getResponseBodyAsString(), e);
            throw new RuntimeException("주식 정보 조회 실패: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Error occurred while getting stock info", e);
            throw new RuntimeException("주식 정보 조회 중 오류 발생", e);
        }
    }
}
