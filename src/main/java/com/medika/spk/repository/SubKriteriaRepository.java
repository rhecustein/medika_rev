package com.medika.spk.repository;

import com.medika.spk.entity.SubKriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubKriteriaRepository extends JpaRepository<SubKriteria, Long> {
    List<SubKriteria> findByKriteriaIdOrderByNilaiDesc(Long kriteriaId);
    void deleteByKriteriaId(Long kriteriaId);

    @Query("SELECT MIN(s.nilai) FROM SubKriteria s WHERE s.kriteria.id = :kriteriaId")
    Optional<Double> findMinNilaiByKriteriaId(@Param("kriteriaId") Long kriteriaId);

    @Query("SELECT MAX(s.nilai) FROM SubKriteria s WHERE s.kriteria.id = :kriteriaId")
    Optional<Double> findMaxNilaiByKriteriaId(@Param("kriteriaId") Long kriteriaId);
}
