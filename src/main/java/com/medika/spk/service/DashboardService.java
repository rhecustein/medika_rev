package com.medika.spk.service;

import com.medika.spk.dto.response.DashboardResponse;
import com.medika.spk.entity.Kandidat;
import com.medika.spk.repository.KriteriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final KandidatService kandidatService;
    private final PenilaianService penilaianService;
    private final KriteriaRepository kriteriaRepo;
    private final HasilService hasilService;

    public DashboardResponse getStats() {
        long totalKandidat   = kandidatService.countAll();
        long diterima        = kandidatService.countByStatus(Kandidat.StatusKandidat.DITERIMA);
        long ditolak         = kandidatService.countByStatus(Kandidat.StatusKandidat.DITOLAK);
        long proses          = kandidatService.countByStatus(Kandidat.StatusKandidat.PROSES);
        long sudahDinilai    = penilaianService.countSudahDinilai();
        long totalKriteria   = kriteriaRepo.count();

        return DashboardResponse.builder()
                .totalKandidat(totalKandidat)
                .kandidatDiterima(diterima)
                .kandidatDitolak(ditolak)
                .kandidatProses(proses)
                .totalKriteria(totalKriteria)
                .sudahDinilai(sudahDinilai)
                .belumDinilai(totalKandidat - sudahDinilai)
                .hasilTersedia(hasilService.hasilTersedia())
                .build();
    }
}
