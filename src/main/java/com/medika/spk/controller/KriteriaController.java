package com.medika.spk.controller;

import com.medika.spk.dto.request.KriteriaRequest;
import com.medika.spk.dto.response.ApiResponse;
import com.medika.spk.service.KriteriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kriteria")
@RequiredArgsConstructor
public class KriteriaController {

    private final KriteriaService kriteriaService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(ApiResponse.success(kriteriaService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(kriteriaService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody KriteriaRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Kriteria berhasil ditambahkan", kriteriaService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody KriteriaRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Kriteria berhasil diupdate", kriteriaService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        kriteriaService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Kriteria berhasil dihapus", null));
    }
}
