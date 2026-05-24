package com.medika.spk.service;

import com.medika.spk.dto.response.SmartResultResponse;
import com.medika.spk.entity.*;
import com.medika.spk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HasilService {

    private final HasilPerhitunganRepository hasilRepo;
    private final DetailPerhitunganRepository detailRepo;
    private final KandidatRepository kandidatRepo;

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<SmartResultResponse> getRanking() {
        List<HasilPerhitungan> hasil = hasilRepo.findAllByOrderByRankingAsc();
        return hasil.stream().map(h -> {
            List<SmartResultResponse.DetailResponse> details = h.getDetails().stream()
                    .map(d -> SmartResultResponse.DetailResponse.builder()
                            .kriteriaId(d.getKriteria().getId())
                            .kodeKriteria(d.getKriteria().getKode())
                            .namaKriteria(d.getKriteria().getNamaKriteria())
                            .cOut(d.getCOut())
                            .cMin(d.getCMin())
                            .cMax(d.getCMax())
                            .utility(d.getUtility())
                            .bobotNormalisasi(d.getBobotNormalisasi())
                            .wjXUtility(d.getWjXUtility())
                            .build())
                    .collect(Collectors.toList());

            return SmartResultResponse.builder()
                    .kandidatId(h.getKandidat().getId())
                    .namaKandidat(h.getKandidat().getNama())
                    .nilaiAkhir(h.getNilaiAkhir())
                    .ranking(h.getRanking())
                    .status(h.getKandidat().getStatus().name())
                    .details(details)
                    .build();
        }).collect(Collectors.toList());
    }

    public void updateStatusKandidat(Long hasilId, String status) {
        HasilPerhitungan hasil = hasilRepo.findById(hasilId)
                .orElseThrow(() -> new RuntimeException("Hasil tidak ditemukan"));
        hasil.getKandidat().setStatus(Kandidat.StatusKandidat.valueOf(status));
        kandidatRepo.save(hasil.getKandidat());
    }

    public boolean hasilTersedia() {
        return hasilRepo.count() > 0;
    }
}
