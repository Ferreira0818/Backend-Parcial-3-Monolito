package com.poe.monolito.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo no válido")
    String email,

    @NotBlank(message = "La contraseña es obligatoria")
    String password
) {}