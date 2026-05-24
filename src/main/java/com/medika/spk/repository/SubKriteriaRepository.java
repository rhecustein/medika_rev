package com.medika.spk.repository;

import com.medika.spk.entity.SubKriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubKriteriaRepository extends JpaRepository<SubKriteria, Long> {
    List<SubKriteria> findByKriteriaIdOrderByNilaiDesc(Long kriteriaId);
    void deleteByKriteriaId(Long kriteriaId);
}
