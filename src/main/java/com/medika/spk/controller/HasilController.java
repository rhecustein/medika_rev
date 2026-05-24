package com.medika.spk.controller;

import com.medika.spk.dto.response.ApiResponse;
import com.medika.spk.service.HasilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/hasil")
@RequiredArgsConstructor
public class HasilController {

    private final HasilService hasilService;

    @GetMapping("/ranking")
    public ResponseEntity<?> getRanking() {
        return ResponseEntity.ok(ApiResponse.success(hasilService.getRanking()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        hasilService.updateStatusKandidat(id, body.get("status"));
        return ResponseEntity.ok(ApiResponse.success("Status berhasil diupdate", null));
    }
}
