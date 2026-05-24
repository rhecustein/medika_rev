package com.medika.spk.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubKriteriaRequest {
    @NotNull(message = "Kriteria ID tidak boleh kosong")
    private Long kriteriaId;
    @NotBlank(message = "Nama sub kriteria tidak boleh kosong")
    private String namaSub;
    @NotNull(message = "Nilai tidak boleh kosong")
    private Double nilai;
}
