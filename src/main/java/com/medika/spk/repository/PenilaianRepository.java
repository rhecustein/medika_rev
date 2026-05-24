package com.medika.spk.repository;

import com.medika.spk.entity.Penilaian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PenilaianRepository extends JpaRepository<Penilaian, Long> {

    Optional<Penilaian> findByKandidatIdAndKriteriaId(Long kandidatId, Long kriteriaId);

    List<Penilaian> findByKandidatId(Long kandidatId);

    @Query("SELECT MIN(p.nilaiInput) FROM Penilaian p WHERE p.kriteria.id = :kriteriaId")
    Optional<Double> findMinNilaiByKriteriaId(@Param("kriteriaId") Long kriteriaId);

    @Query("SELECT MAX(p.nilaiInput) FROM Penilaian p WHERE p.kriteria.id = :kriteriaId")
    Optional<Double> findMaxNilaiByKriteriaId(@Param("kriteriaId") Long kriteriaId);

    boolean existsByKandidatId(Long kandidatId);

    @Query("SELECT COUNT(DISTINCT p.kandidat.id) FROM Penilaian p")
    long countDistinctKandidat();

    void deleteByKandidatId(Long kandidatId);

    @Query("SELECT DISTINCT p.kandidat.id FROM Penilaian p")
    List<Long> findEvaluatedKandidatIds();
}
