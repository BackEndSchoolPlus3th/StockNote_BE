package org.com.stocknote.domain.stockApi.index.controller;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.stockApi.index.dto.CurrentIndexResponseDto;
import org.com.stocknote.domain.stockApi.index.dto.StockIndexDto;
import org.com.stocknote.domain.stockApi.index.service.CurrentIndexService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class CurrentIndexController {
    private final CurrentIndexService currentIndexService;

    // 전체 정보 불러오기
    @GetMapping("/api/kospi")
    public Mono<CurrentIndexResponseDto> getKospiData() {
        return currentIndexService.getKOSPI();
    }

    @GetMapping("/api/kosdaq")
    public Mono<CurrentIndexResponseDto> getKosdaqData() {
        return currentIndexService.getKOSDAQ();
    }

    @GetMapping("/api/kospi200")
    public Mono<CurrentIndexResponseDto> getKospi200Data() {
        return currentIndexService.getKOSPI200();
    }


    // 필터링한 정보 불러오기
    @GetMapping("/api/filtered/kospi")
    public Mono<StockIndexDto> getFilteredKOSPIData() {
        return currentIndexService.getFilteredKOSPI();
    }

    @GetMapping("/api/filtered/kosdaq")
    public Mono<StockIndexDto> getFilteredKOSDAQData() {
        return currentIndexService.getFilteredKOSDAQ();
    }

    @GetMapping("/api/filtered/kospi200")
    public Mono<StockIndexDto> getFilteredKOSPI200Data() {
        return currentIndexService.getFilteredKOSPI200();
    }
}
