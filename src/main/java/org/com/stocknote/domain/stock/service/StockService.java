package org.com.stocknote.domain.stock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.stock.PeriodType;
import org.com.stocknote.domain.stock.dto.StockDailyResponse;
import org.com.stocknote.domain.stock.dto.StockPriceResponse;
import org.com.stocknote.domain.stock.dto.StockTimeResponse;
import org.com.stocknote.global.kis.KisKeyManager;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class StockService {

    private final KisKeyManager kisKeyManager;
    private final RestTemplate restTemplate;

    public StockService(KisKeyManager keyManager, RestTemplate restTemplate) {
        this.kisKeyManager = keyManager;
        this.restTemplate = restTemplate; // RestTemplate 주입
    }

    public StockPriceResponse getStockPrice(String stockCode) {
        String baseUrl = "https://openapivts.koreainvestment.com:29443";  // Change to production URL if needed
        String endpoint = "/uapi/domestic-stock/v1/quotations/inquire-price";

        // Build URL with query parameters
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + endpoint)
                .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                .queryParam("FID_INPUT_ISCD", stockCode)
                .toUriString();

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + kisKeyManager.getAccessToken());
        headers.set("appkey", kisKeyManager.getAppKey());
        headers.set("appsecret", kisKeyManager.getAppSecret());
        headers.set("tr_id", "FHKST01010100");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<StockPriceResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    StockPriceResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to get stock price. Status code: " + response.getStatusCode());
            }
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("주식 가격 조회 실패: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("주식 가격 조회 중 오류 발생", e);
        }
    }


    public StockDailyResponse getStockPrices(String stockCode, PeriodType periodType, LocalDate startDate, LocalDate endDate) {
        String baseUrl = "https://openapivts.koreainvestment.com:29443";
        String endpoint = "/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice";

        // 날짜 포맷 변환
        String formattedStartDate = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String formattedEndDate = endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // URL 구성
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + endpoint)
                .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                .queryParam("FID_INPUT_ISCD", stockCode)
                .queryParam("FID_INPUT_DATE_1", formattedStartDate)
                .queryParam("FID_INPUT_DATE_2", formattedEndDate)
                .queryParam("FID_PERIOD_DIV_CODE", periodType.getCode())
                .queryParam("FID_ORG_ADJ_PRC", "0")
                .build(false)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + kisKeyManager.getAccessToken());
        headers.set("appkey", kisKeyManager.getAppKey());
        headers.set("appsecret", kisKeyManager.getAppSecret());
        headers.set("tr_id", "FHKST03010100");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> rawResponse = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            log.debug("API Response for {} data: {}", periodType, rawResponse.getBody());

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(rawResponse.getBody(), StockDailyResponse.class);
        } catch (Exception e) {
            log.error("API Error for stockCode {} with period {}: {}", stockCode, periodType, e.getMessage());
            throw new RuntimeException("주식 데이터 조회 중 오류 발생", e);
        }
    }

    // 일별 데이터 조회
    public StockDailyResponse getDailyStockPrices(String stockCode, LocalDate startDate, LocalDate endDate) {
        return getStockPrices(stockCode, PeriodType.DAILY, startDate, endDate);
    }

    // 주별 데이터 조회
    public StockDailyResponse getWeeklyStockPrices(String stockCode, LocalDate startDate, LocalDate endDate) {
        return getStockPrices(stockCode, PeriodType.WEEKLY, startDate, endDate);
    }

    // 월별 데이터 조회
    public StockDailyResponse getMonthlyStockPrices(String stockCode, LocalDate startDate, LocalDate endDate) {
        return getStockPrices(stockCode, PeriodType.MONTHLY, startDate, endDate);
    }

    // 연간 데이터 조회
    public StockDailyResponse getYearlyStockPrices(String stockCode, LocalDate startDate, LocalDate endDate) {
        return getStockPrices(stockCode, PeriodType.YEARLY, startDate, endDate);
    }

    public StockTimeResponse getTimeStockPrices(String stockCode) {
        String baseUrl = "https://openapivts.koreainvestment.com:29443";
        String endpoint = "/uapi/domestic-stock/v1/quotations/inquire-time-itemconclusion";

        // 현재 시간 계산
        LocalTime now = LocalTime.now();
        String formattedTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));

        // URL 구성
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + endpoint)
                .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                .queryParam("FID_INPUT_ISCD", stockCode)
                .queryParam("FID_INPUT_HOUR_1", formattedTime)
                .build(false)
                .toUriString();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + kisKeyManager.getAccessToken());
        headers.set("appkey", kisKeyManager.getAppKey());
        headers.set("appsecret", kisKeyManager.getAppSecret());
        headers.set("tr_id", "FHPST01060000");  // 시간대별 체결 조회 TR_ID

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            // 먼저 String으로 응답을 받아서 로깅
            ResponseEntity<String> rawResponse = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            log.debug("API Response: {}", rawResponse.getBody());

            // ObjectMapper로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            StockTimeResponse response = objectMapper.readValue(
                    rawResponse.getBody(),
                    StockTimeResponse.class
            );

            if (response == null || response.getOutput2() == null || response.getOutput2().isEmpty()) {
                log.warn("No time data returned for stockCode: {}", stockCode);
            }

            return response;
        } catch (Exception e) {
            log.error("API Error for stockCode {}: {}", stockCode, e.getMessage());
            throw new RuntimeException("시간대별 주식 데이터 조회 중 오류 발생", e);
        }
    }




}
