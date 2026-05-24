package com.medika.spk.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PenilaianRequest {
    @NotNull
    private Long kandidatId;
    @NotNull
    private List<PenilaianItemRequest> items;

    @Data
    public static class PenilaianItemRequest {
        private Long kriteriaId;
        private Long subKriteriaId;
        private Double nilaiInput;
    }
}
