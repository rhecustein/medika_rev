package com.medika.spk.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detail_perhitungan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DetailPerhitungan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hasil_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private HasilPerhitungan hasilPerhitungan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kriteria_id", nullable = false)
    private Kriteria kriteria;

    @Column(name = "c_out")
    private Double cOut;

    @Column(name = "c_min")
    private Double cMin;

    @Column(name = "c_max")
    private Double cMax;

    private Double utility;

    @Column(name = "bobot_normalisasi")
    private Double bobotNormalisasi;

    @Column(name = "wj_x_utility")
    private Double wjXUtility;
}
