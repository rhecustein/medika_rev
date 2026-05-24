package com.medika.spk.service;

import com.medika.spk.dto.request.KriteriaRequest;
import com.medika.spk.entity.Kriteria;
import com.medika.spk.repository.KriteriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KriteriaService {

    private final KriteriaRepository kriteriaRepo;

    public List<Kriteria> findAll() {
        return kriteriaRepo.findAllByOrderByKodeAsc();
    }

    public Kriteria findById(Long id) {
        return kriteriaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Kriteria tidak ditemukan"));
    }

    public Kriteria create(KriteriaRequest req) {
        Kriteria k = Kriteria.builder()
                .kode(req.getKode())
                .namaKriteria(req.getNamaKriteria())
                .bobot(req.getBobot())
                .atribut(Kriteria.Atribut.valueOf(req.getAtribut()))
                .build();
        return kriteriaRepo.save(k);
    }

    public Kriteria update(Long id, KriteriaRequest req) {
        Kriteria k = findById(id);
        k.setKode(req.getKode());
        k.setNamaKriteria(req.getNamaKriteria());
        k.setBobot(req.getBobot());
        k.setAtribut(Kriteria.Atribut.valueOf(req.getAtribut()));
        return kriteriaRepo.save(k);
    }

    public void delete(Long id) {
        kriteriaRepo.deleteById(id);
    }

    public double getTotalBobot() {
        return kriteriaRepo.findAll().stream()
                .mapToDouble(Kriteria::getBobot).sum();
    }
}
