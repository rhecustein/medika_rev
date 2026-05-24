package com.medika.spk.service;

import com.medika.spk.dto.request.KandidatRequest;
import com.medika.spk.entity.Kandidat;
import com.medika.spk.repository.KandidatRepository;
import com.medika.spk.repository.PenilaianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class KandidatService {

    private final KandidatRepository kandidatRepo;
    private final PenilaianRepository penilaianRepo;

    public Page<Kandidat> findAll(String search, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        if (status != null && !status.isBlank()) {
            return kandidatRepo.findBySearchAndStatus(search, Kandidat.StatusKandidat.valueOf(status), pageable);
        }
        return kandidatRepo.findBySearch(search, pageable);
    }

    public Kandidat findById(Long id) {
        return kandidatRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Kandidat tidak ditemukan"));
    }

    public Kandidat create(KandidatRequest req) {
        Kandidat k = Kandidat.builder()
                .nama(req.getNama())
                .jenisKelamin(req.getJenisKelamin() != null
                        ? Kandidat.JenisKelamin.valueOf(req.getJenisKelamin()) : null)
                .pendidikanTerakhir(req.getPendidikanTerakhir())
                .tanggalDaftar(req.getTanggalDaftar() != null
                        ? req.getTanggalDaftar() : LocalDate.now())
                .status(Kandidat.StatusKandidat.PROSES)
                .build();
        return kandidatRepo.save(k);
    }

    public Kandidat update(Long id, KandidatRequest req) {
        Kandidat k = findById(id);
        k.setNama(req.getNama());
        if (req.getJenisKelamin() != null) {
            k.setJenisKelamin(Kandidat.JenisKelamin.valueOf(req.getJenisKelamin()));
        }
        k.setPendidikanTerakhir(req.getPendidikanTerakhir());
        if (req.getTanggalDaftar() != null) k.setTanggalDaftar(req.getTanggalDaftar());
        if (req.getStatus() != null) k.setStatus(Kandidat.StatusKandidat.valueOf(req.getStatus()));
        return kandidatRepo.save(k);
    }

    @Transactional
    public void delete(Long id) {
        penilaianRepo.deleteByKandidatId(id);
        kandidatRepo.deleteById(id);
    }

    public void updateStatus(Long id, String status) {
        Kandidat k = findById(id);
        k.setStatus(Kandidat.StatusKandidat.valueOf(status));
        kandidatRepo.save(k);
    }

    public long countAll() { return kandidatRepo.count(); }
    public long countByStatus(Kandidat.StatusKandidat status) { return kandidatRepo.countByStatus(status); }
}
