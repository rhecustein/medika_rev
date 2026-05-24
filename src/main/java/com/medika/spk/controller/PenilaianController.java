package com.medika.spk.controller;

import com.medika.spk.dto.request.PenilaianRequest;
import com.medika.spk.dto.response.ApiResponse;
import com.medika.spk.service.PenilaianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/penilaian")
@RequiredArgsConstructor
public class PenilaianController {

    private final PenilaianService penilaianService;

    @GetMapping("/kandidat/{kandidatId}")
    public ResponseEntity<?> getByKandidat(@PathVariable Long kandidatId) {
        return ResponseEntity.ok(ApiResponse.success(penilaianService.findByKandidat(kandidatId)));
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@Valid @RequestBody PenilaianRequest req) {
        penilaianService.saveAll(req);
        return ResponseEntity.ok(ApiResponse.success("Penilaian berhasil disimpan", null));
    }

    @GetMapping("/status/{kandidatId}")
    public ResponseEntity<?> getStatus(@PathVariable Long kandidatId) {
        return ResponseEntity.ok(ApiResponse.success(penilaianService.sudahDinilai(kandidatId)));
    }

    @GetMapping("/evaluated-ids")
    public ResponseEntity<?> getEvaluatedIds() {
        return ResponseEntity.ok(ApiResponse.success(penilaianService.getEvaluatedKandidatIds()));
    }
}
