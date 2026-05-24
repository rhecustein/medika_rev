package com.medika.spk.service;

import com.medika.spk.dto.request.SubKriteriaRequest;
import com.medika.spk.entity.*;
import com.medika.spk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubKriteriaService {

    private final SubKriteriaRepository subKriteriaRepo;
    private final KriteriaRepository kriteriaRepo;

    public List<SubKriteria> findByKriteria(Long kriteriaId) {
        return subKriteriaRepo.findByKriteriaIdOrderByNilaiDesc(kriteriaId);
    }

    public SubKriteria findById(Long id) {
        return subKriteriaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Sub kriteria tidak ditemukan"));
    }

    public SubKriteria create(SubKriteriaRequest req) {
        Kriteria kriteria = kriteriaRepo.findById(req.getKriteriaId())
                .orElseThrow(() -> new RuntimeException("Kriteria tidak ditemukan"));
        SubKriteria sk = SubKriteria.builder()
                .kriteria(kriteria)
                .namaSub(req.getNamaSub())
                .nilai(req.getNilai())
                .build();
        return subKriteriaRepo.save(sk);
    }

    public SubKriteria update(Long id, SubKriteriaRequest req) {
        SubKriteria sk = findById(id);
        sk.setNamaSub(req.getNamaSub());
        sk.setNilai(req.getNilai());
        return subKriteriaRepo.save(sk);
    }

    public void delete(Long id) {
        subKriteriaRepo.deleteById(id);
    }
}
