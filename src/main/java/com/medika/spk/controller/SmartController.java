package com.medika.spk.controller;

import com.medika.spk.dto.response.ApiResponse;
import com.medika.spk.service.SmartCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/smart")
@RequiredArgsConstructor
public class SmartController {

    private final SmartCalculationService smartService;

    @GetMapping("/normalisasi")
    public ResponseEntity<?> getNormalisasi() {
        return ResponseEntity.ok(ApiResponse.success(smartService.hitungNormalisasiBobot()));
    }

    @GetMapping("/utility/{kandidatId}")
    public ResponseEntity<?> getUtility(@PathVariable Long kandidatId) {
        return ResponseEntity.ok(ApiResponse.success(smartService.hitungUtility(kandidatId)));
    }

    @GetMapping("/nilai-akhir/{kandidatId}")
    public ResponseEntity<?> getNilaiAkhir(@PathVariable Long kandidatId) {
        return ResponseEntity.ok(ApiResponse.success(smartService.hitungNilaiAkhir(kandidatId)));
    }

    @GetMapping("/detail/{kandidatId}")
    public ResponseEntity<?> getDetail(@PathVariable Long kandidatId) {
        return ResponseEntity.ok(ApiResponse.success(smartService.getDetailKandidat(kandidatId)));
    }

    @PostMapping("/hitung-semua")
    public ResponseEntity<?> hitungSemua() {
        return ResponseEntity.ok(ApiResponse.success("Perhitungan selesai", smartService.hitungSemuaKandidat()));
    }
}
