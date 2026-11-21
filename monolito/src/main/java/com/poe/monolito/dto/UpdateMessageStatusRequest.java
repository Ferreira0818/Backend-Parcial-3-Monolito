package com.poe.monolito.dto;

import com.poe.monolito.entity.enums.MessageStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateMessageStatusRequest(
    @NotNull(message = "El nuevo estado del mensaje es obligatorio")
    MessageStatus status
) {}