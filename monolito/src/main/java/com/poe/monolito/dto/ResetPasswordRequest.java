package com.poe.monolito.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
    @NotBlank(message = "El correo registrado es obligatorio")
    @Email(message = "Formato de correo no válido")
    String email,

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 4, message = "Mínimo 4 caracteres")
    String newPassword
) {}