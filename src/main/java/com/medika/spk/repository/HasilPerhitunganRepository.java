package com.medika.spk.repository;

import com.medika.spk.entity.HasilPerhitungan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HasilPerhitunganRepository extends JpaRepository<HasilPerhitungan, Long> {
    List<HasilPerhitungan> findAllByOrderByRankingAsc();
    Optional<HasilPerhitungan> findByKandidatId(Long kandidatId);
    boolean existsByKandidatId(Long kandidatId);
}
