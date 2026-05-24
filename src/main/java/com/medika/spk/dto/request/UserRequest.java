package com.medika.spk.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "Nama tidak boleh kosong")
    private String nama;
    @NotBlank(message = "Username tidak boleh kosong")
    private String username;
    private String password;
    private String email;
    @NotBlank(message = "Role tidak boleh kosong")
    private String role;
    private String status;
}
