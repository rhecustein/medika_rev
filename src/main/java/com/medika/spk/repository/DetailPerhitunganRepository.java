package com.medika.spk.repository;

import com.medika.spk.entity.DetailPerhitungan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailPerhitunganRepository extends JpaRepository<DetailPerhitungan, Long> {
    List<DetailPerhitungan> findByHasilPerhitunganId(Long hasilId);
}
