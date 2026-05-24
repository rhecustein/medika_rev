package com.medika.spk.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sub_kriteria")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SubKriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kriteria_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Kriteria kriteria;

    @Column(name = "nama_sub", nullable = false)
    private String namaSub;

    @Column(nullable = false)
    private Double nilai;
}
