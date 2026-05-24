package com.medika.spk.service;

import com.medika.spk.dto.request.PenilaianRequest;
import com.medika.spk.entity.*;
import com.medika.spk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PenilaianService {

    private final PenilaianRepository penilaianRepo;
    private final KandidatRepository kandidatRepo;
    private final KriteriaRepository kriteriaRepo;
    private final SubKriteriaRepository subKriteriaRepo;
    private final UserRepository userRepo;

    public List<Penilaian> findByKandidat(Long kandidatId) {
        return penilaianRepo.findByKandidatId(kandidatId);
    }

    @Transactional
    public void saveAll(PenilaianRequest req) {
        Kandidat kandidat = kandidatRepo.findById(req.getKandidatId())
                .orElseThrow(() -> new RuntimeException("Kandidat tidak ditemukan"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User penilai = userRepo.findByUsername(username).orElse(null);

        for (PenilaianRequest.PenilaianItemRequest item : req.getItems()) {
            Kriteria kriteria = kriteriaRepo.findById(item.getKriteriaId())
                    .orElseThrow(() -> new RuntimeException("Kriteria tidak ditemukan"));

            double nilaiInput = item.getNilaiInput() != null ? item.getNilaiInput() : 0.0;

            // Jika sub kriteria dipilih, ambil nilai dari sub kriteria
            SubKriteria subKriteria = null;
            if (item.getSubKriteriaId() != null) {
                subKriteria = subKriteriaRepo.findById(item.getSubKriteriaId()).orElse(null);
                if (subKriteria != null) {
                    nilaiInput = subKriteria.getNilai();
                }
            }

            // Upsert: cari existing penilaian, update atau buat baru
            Penilaian penilaian = penilaianRepo
                    .findByKandidatIdAndKriteriaId(kandidat.getId(), kriteria.getId())
                    .orElse(Penilaian.builder().kandidat(kandidat).kriteria(kriteria).build());

            penilaian.setSubKriteria(subKriteria);
            penilaian.setNilaiInput(nilaiInput);
            penilaian.setDinilaiOleh(penilai);
            penilaianRepo.save(penilaian);
        }
    }

    public boolean sudahDinilai(Long kandidatId) {
        return penilaianRepo.existsByKandidatId(kandidatId);
    }

    public List<Long> getEvaluatedKandidatIds() {
        return penilaianRepo.findEvaluatedKandidatIds();
    }

    public long countSudahDinilai() {
        return penilaianRepo.countDistinctKandidat();
    }
}
