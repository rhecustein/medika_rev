package com.medika.spk.controller;

import com.medika.spk.dto.request.SubKriteriaRequest;
import com.medika.spk.dto.response.ApiResponse;
import com.medika.spk.service.SubKriteriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sub-kriteria")
@RequiredArgsConstructor
public class SubKriteriaController {

    private final SubKriteriaService subKriteriaService;

    @GetMapping
    public ResponseEntity<?> getByKriteria(@RequestParam Long kriteriaId) {
        return ResponseEntity.ok(ApiResponse.success(subKriteriaService.findByKriteria(kriteriaId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(subKriteriaService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SubKriteriaRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Sub kriteria berhasil ditambahkan", subKriteriaService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody SubKriteriaRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Sub kriteria berhasil diupdate", subKriteriaService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        subKriteriaService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Sub kriteria berhasil dihapus", null));
    }
}
