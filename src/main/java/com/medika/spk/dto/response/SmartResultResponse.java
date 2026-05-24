package com.medika.spk.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SmartResultResponse {
    private Long kandidatId;
    private String namaKandidat;
    private Double nilaiAkhir;
    private Integer ranking;
    private String status;
    private List<DetailResponse> details;
    private Map<Long, Double> normalisasiBobot;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DetailResponse {
        private Long kriteriaId;
        private String kodeKriteria;
        private String namaKriteria;
        private Double cOut;
        private Double cMin;
        private Double cMax;
        private Double utility;
        private Double bobotNormalisasi;
        private Double wjXUtility;
    }
}
