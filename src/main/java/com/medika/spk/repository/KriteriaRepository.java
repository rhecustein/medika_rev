package com.medika.spk.repository;

import com.medika.spk.entity.Kriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KriteriaRepository extends JpaRepository<Kriteria, Long> {
    List<Kriteria> findAllByOrderByKodeAsc();
    boolean existsByKode(String kode);
}
