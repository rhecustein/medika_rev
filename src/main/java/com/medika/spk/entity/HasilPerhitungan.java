package com.medika.spk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hasil_perhitungan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HasilPerhitungan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kandidat_id", nullable = false)
    private Kandidat kandidat;

    @Column(name = "nilai_akhir", nullable = false)
    private Double nilaiAkhir;

    private Integer ranking;

    @Column(name = "tanggal_hitung")
    @Builder.Default
    private LocalDateTime tanggalHitung = LocalDateTime.now();

    @OneToMany(mappedBy = "hasilPerhitungan", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<DetailPerhitungan> details;
}
