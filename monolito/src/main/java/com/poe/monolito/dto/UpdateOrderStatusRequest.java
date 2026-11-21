package com.poe.monolito.dto;

import com.poe.monolito.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
    @NotNull(message = "El nuevo estado es obligatorio")
    OrderStatus status
) {}