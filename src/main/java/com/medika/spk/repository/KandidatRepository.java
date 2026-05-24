package com.medika.spk.repository;

import com.medika.spk.entity.Kandidat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KandidatRepository extends JpaRepository<Kandidat, Long> {

    @Query("SELECT k FROM Kandidat k WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(k.nama) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Kandidat> findBySearch(@Param("search") String search, Pageable pageable);

    @Query("SELECT k FROM Kandidat k WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(k.nama) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND k.status = :status")
    Page<Kandidat> findBySearchAndStatus(@Param("search") String search,
                                         @Param("status") Kandidat.StatusKandidat status,
                                         Pageable pageable);

    long countByStatus(Kandidat.StatusKandidat status);
}
