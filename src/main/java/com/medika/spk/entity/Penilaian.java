package com.medika.spk.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "penilaian",
       uniqueConstraints = @UniqueConstraint(columnNames = {"kandidat_id", "kriteria_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Penilaian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kandidat_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Kandidat kandidat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kriteria_id", nullable = false)
    private Kriteria kriteria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_kriteria_id")
    private SubKriteria subKriteria;

    @Column(name = "nilai_input", nullable = false)
    private Double nilaiInput;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dinilai_oleh")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User dinilaiOleh;
}
