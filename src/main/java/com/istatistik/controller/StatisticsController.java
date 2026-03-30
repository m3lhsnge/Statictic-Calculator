package com.istatistik.controller;

import com.istatistik.dto.DataRequest;
import com.istatistik.dto.StatisticsResponse;
import com.istatistik.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatisticsController {

    private final StatisticsService istatistikServisi;

    // Spring Boot otomatik olarak servisi buraya enjekte eder
    public StatisticsController(StatisticsService istatistikServisi) {
        this.istatistikServisi = istatistikServisi;
    }

    // POST /api/calculate - Frontend'den gelen veriyi alıp hesaplar
    @PostMapping("/calculate")
    public ResponseEntity<?> hesapla(@RequestBody DataRequest istek) {
        try {
            StatisticsResponse sonuc = istatistikServisi.hesapla(istek);
            return ResponseEntity.ok(sonuc);
        } catch (IllegalArgumentException hata) {
            return ResponseEntity.badRequest().body(Map.of("error", hata.getMessage()));
        } catch (Exception hata) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Sunucu hatası: " + hata.getMessage()));
        }
    }
}
