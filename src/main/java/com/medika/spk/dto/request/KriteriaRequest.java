package com.medika.spk.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KriteriaRequest {
    @NotBlank(message = "Kode tidak boleh kosong")
    private String kode;
    @NotBlank(message = "Nama kriteria tidak boleh kosong")
    private String namaKriteria;
    @NotNull(message = "Bobot tidak boleh kosong")
    private Double bobot;
    @NotBlank(message = "Atribut tidak boleh kosong")
    private String atribut;
}
