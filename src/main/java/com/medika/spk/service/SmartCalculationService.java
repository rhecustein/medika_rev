package com.medika.spk.service;

import com.medika.spk.dto.response.SmartResultResponse;
import com.medika.spk.entity.*;
import com.medika.spk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SmartCalculationService {

    private final KriteriaRepository kriteriaRepo;
    private final PenilaianRepository penilaianRepo;
    private final KandidatRepository kandidatRepo;
    private final HasilPerhitunganRepository hasilRepo;
    private final DetailPerhitunganRepository detailRepo;

    // ── Step 1: Normalisasi Bobot Wj = wj / Σwj ─────────────────────────────
    public Map<Long, Double> hitungNormalisasiBobot() {
        List<Kriteria> list = kriteriaRepo.findAllByOrderByKodeAsc();
        double total = list.stream().mapToDouble(Kriteria::getBobot).sum();

        Map<Long, Double> result = new LinkedHashMap<>();
        for (Kriteria k : list) {
            result.put(k.getId(), round4(k.getBobot() / total));
        }
        return result;
    }

    // ── Step 2: Utility per kriteria per kandidat ────────────────────────────
    // Benefit: uj = (Cout - Cmin) / (Cmax - Cmin)
    // Cost:    uj = (Cmax - Cout) / (Cmax - Cmin)
    public Map<Long, Double> hitungUtility(Long kandidatId) {
        List<Kriteria> list = kriteriaRepo.findAllByOrderByKodeAsc();
        Map<Long, Double> result = new LinkedHashMap<>();

        for (Kriteria k : list) {
            double cout = getNilaiKandidat(kandidatId, k.getId());
            double cmin = getCmin(k.getId());
            double cmax = getCmax(k.getId());

            double utility;
            if (Double.compare(cmax, cmin) == 0) {
                utility = 1.0;
            } else if (k.getAtribut() == Kriteria.Atribut.BENEFIT) {
                utility = (cout - cmin) / (cmax - cmin);
            } else {
                utility = (cmax - cout) / (cmax - cmin);
            }
            result.put(k.getId(), round4(utility));
        }
        return result;
    }

    // ── Step 3: Nilai Akhir U(ai) = Σ Wj × uj(ai) ──────────────────────────
    public double hitungNilaiAkhir(Long kandidatId) {
        Map<Long, Double> wj = hitungNormalisasiBobot();
        Map<Long, Double> uj = hitungUtility(kandidatId);

        double total = 0.0;
        for (Map.Entry<Long, Double> e : wj.entrySet()) {
            total += e.getValue() * uj.getOrDefault(e.getKey(), 0.0);
        }
        return round4(total);
    }

    // ── Step 4: Hitung semua kandidat + simpan hasil + ranking ──────────────
    @Transactional
    public List<SmartResultResponse> hitungSemuaKandidat() {
        List<Kandidat> semuaKandidat = kandidatRepo.findAll();
        if (semuaKandidat.isEmpty()) throw new RuntimeException("Tidak ada kandidat");

        // Hapus hasil lama
        hasilRepo.deleteAll();

        List<Kriteria> kriteriaList = kriteriaRepo.findAllByOrderByKodeAsc();
        Map<Long, Double> normalisasi = hitungNormalisasiBobot();

        List<HasilPerhitungan> hasilList = new ArrayList<>();

        for (Kandidat kandidat : semuaKandidat) {
            boolean adaPenilaian = penilaianRepo.existsByKandidatId(kandidat.getId());
            if (!adaPenilaian) continue;

            Map<Long, Double> utilityMap = hitungUtility(kandidat.getId());
            double nilaiAkhir = 0.0;
            List<DetailPerhitungan> details = new ArrayList<>();

            for (Kriteria k : kriteriaList) {
                double cout  = getNilaiKandidat(kandidat.getId(), k.getId());
                double cmin  = getCmin(k.getId());
                double cmax  = getCmax(k.getId());
                double wj    = normalisasi.get(k.getId());
                double uj    = utilityMap.get(k.getId());
                double wjxuj = round4(wj * uj);
                nilaiAkhir  += wjxuj;

                DetailPerhitungan detail = DetailPerhitungan.builder()
                        .kriteria(k)
                        .cOut(cout)
                        .cMin(cmin)
                        .cMax(cmax)
                        .utility(uj)
                        .bobotNormalisasi(wj)
                        .wjXUtility(wjxuj)
                        .build();
                details.add(detail);
            }

            HasilPerhitungan hasil = HasilPerhitungan.builder()
                    .kandidat(kandidat)
                    .nilaiAkhir(round4(nilaiAkhir))
                    .tanggalHitung(LocalDateTime.now())
                    .details(details)
                    .build();

            details.forEach(d -> d.setHasilPerhitungan(hasil));
            hasilList.add(hasil);
        }

        if (hasilList.isEmpty()) throw new RuntimeException("Belum ada penilaian yang tersimpan");

        // Sort DESC → assign ranking
        hasilList.sort((a, b) -> Double.compare(b.getNilaiAkhir(), a.getNilaiAkhir()));
        for (int i = 0; i < hasilList.size(); i++) {
            hasilList.get(i).setRanking(i + 1);
        }

        hasilRepo.saveAll(hasilList);

        return hasilList.stream().map(h -> SmartResultResponse.builder()
                .kandidatId(h.getKandidat().getId())
                .namaKandidat(h.getKandidat().getNama())
                .nilaiAkhir(h.getNilaiAkhir())
                .ranking(h.getRanking())
                .status(h.getKandidat().getStatus().name())
                .build()
        ).collect(Collectors.toList());
    }

    // ── Full detail 1 kandidat untuk tampilan tabel ──────────────────────────
    public SmartResultResponse getDetailKandidat(Long kandidatId) {
        List<Kriteria> kriteriaList = kriteriaRepo.findAllByOrderByKodeAsc();
        Map<Long, Double> normalisasi = hitungNormalisasiBobot();
        Map<Long, Double> utilityMap  = hitungUtility(kandidatId);

        List<SmartResultResponse.DetailResponse> detailList = new ArrayList<>();
        double nilaiAkhir = 0.0;

        for (Kriteria k : kriteriaList) {
            double cout  = getNilaiKandidat(kandidatId, k.getId());
            double cmin  = getCmin(k.getId());
            double cmax  = getCmax(k.getId());
            double wj    = normalisasi.get(k.getId());
            double uj    = utilityMap.get(k.getId());
            double wjxuj = round4(wj * uj);
            nilaiAkhir  += wjxuj;

            detailList.add(SmartResultResponse.DetailResponse.builder()
                    .kriteriaId(k.getId())
                    .kodeKriteria(k.getKode())
                    .namaKriteria(k.getNamaKriteria())
                    .cOut(cout)
                    .cMin(cmin)
                    .cMax(cmax)
                    .utility(uj)
                    .bobotNormalisasi(wj)
                    .wjXUtility(wjxuj)
                    .build());
        }

        Kandidat kandidat = kandidatRepo.findById(kandidatId)
                .orElseThrow(() -> new RuntimeException("Kandidat tidak ditemukan"));

        return SmartResultResponse.builder()
                .kandidatId(kandidatId)
                .namaKandidat(kandidat.getNama())
                .nilaiAkhir(round4(nilaiAkhir))
                .normalisasiBobot(normalisasi)
                .details(detailList)
                .build();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private double getNilaiKandidat(Long kandidatId, Long kriteriaId) {
        return penilaianRepo.findByKandidatIdAndKriteriaId(kandidatId, kriteriaId)
                .map(Penilaian::getNilaiInput)
                .orElse(0.0);
    }

    private double getCmin(Long kriteriaId) {
        return penilaianRepo.findMinNilaiByKriteriaId(kriteriaId).orElse(0.0);
    }

    private double getCmax(Long kriteriaId) {
        return penilaianRepo.findMaxNilaiByKriteriaId(kriteriaId).orElse(100.0);
    }

    private double round4(double val) {
        return Math.round(val * 10000.0) / 10000.0;
    }
}
