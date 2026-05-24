package com.medika.spk.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class KandidatRequest {
    @NotBlank(message = "Nama tidak boleh kosong")
    private String nama;
    private String jenisKelamin;
    private String pendidikanTerakhir;
    private LocalDate tanggalDaftar;
    private String status;
}
