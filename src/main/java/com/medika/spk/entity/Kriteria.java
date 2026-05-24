package com.medika.spk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "kriteria")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Kriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String kode;

    @Column(name = "nama_kriteria", nullable = false)
    private String namaKriteria;

    @Column(nullable = false)
    private Double bobot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Atribut atribut;

    @OneToMany(mappedBy = "kriteria", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<SubKriteria> subKriteriaList;

    public enum Atribut { BENEFIT, COST }
}
