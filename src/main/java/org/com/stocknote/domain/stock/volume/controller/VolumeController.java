package org.com.stocknote.domain.stock.volume.controller;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.stock.volume.dto.VolumeResponseDto;
import org.com.stocknote.domain.stock.volume.service.VolumeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class VolumeController {
    private final VolumeService volumeService;

    @GetMapping("/api/volume")
    public Mono<VolumeResponseDto> getVolume() {
        return volumeService.getVolumeData();
    }
}