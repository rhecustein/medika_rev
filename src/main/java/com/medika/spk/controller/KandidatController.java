package com.medika.spk.controller;

import com.medika.spk.dto.request.KandidatRequest;
import com.medika.spk.dto.response.ApiResponse;
import com.medika.spk.service.KandidatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kandidat")
@RequiredArgsConstructor
public class KandidatController {

    private final KandidatService kandidatService;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(kandidatService.findAll(search, status, page, size)));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        java.util.Map<String, Long> stats = new java.util.LinkedHashMap<>();
        stats.put("total", kandidatService.countAll());
        stats.put("proses", kandidatService.countByStatus(com.medika.spk.entity.Kandidat.StatusKandidat.PROSES));
        stats.put("diterima", kandidatService.countByStatus(com.medika.spk.entity.Kandidat.StatusKandidat.DITERIMA));
        stats.put("ditolak", kandidatService.countByStatus(com.medika.spk.entity.Kandidat.StatusKandidat.DITOLAK));
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(kandidatService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody KandidatRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Kandidat berhasil ditambahkan", kandidatService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody KandidatRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Kandidat berhasil diupdate", kandidatService.update(id, req)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        kandidatService.updateStatus(id, body.get("status"));
        return ResponseEntity.ok(ApiResponse.success("Status diupdate", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        kandidatService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Kandidat berhasil dihapus", null));
    }
}
