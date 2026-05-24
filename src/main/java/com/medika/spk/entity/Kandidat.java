package com.medika.spk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "kandidat")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Kandidat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nama;

    @Enumerated(EnumType.STRING)
    @Column(name = "jenis_kelamin")
    private JenisKelamin jenisKelamin;

    @Column(name = "pendidikan_terakhir")
    private String pendidikanTerakhir;

    @Column(name = "tanggal_daftar")
    private LocalDate tanggalDaftar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusKandidat status = StatusKandidat.PROSES;

    public enum JenisKelamin { LAKI_LAKI, PEREMPUAN }
    public enum StatusKandidat { PROSES, DITERIMA, DITOLAK }
}
