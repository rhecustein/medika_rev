package com.medika.spk.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardResponse {
    private long totalKandidat;
    private long kandidatDiterima;
    private long kandidatDitolak;
    private long kandidatProses;
    private long totalKriteria;
    private long sudahDinilai;
    private long belumDinilai;
    private boolean hasilTersedia;
}
