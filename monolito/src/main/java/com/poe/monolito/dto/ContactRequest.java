package com.poe.monolito.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactRequest(
    @NotBlank(message = "El nombre o IGN es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    String name,

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo no válido")
    String email,

    @NotBlank(message = "El asunto es obligatorio")
    @Size(min = 3, max = 150, message = "El asunto debe tener entre 3 y 150 caracteres")
    String subject,

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(min = 10, message = "El mensaje debe tener al menos 10 caracteres")
    String message
) {}