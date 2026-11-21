package com.poe.monolito.dto;

import com.poe.monolito.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long id,
    String orderNumber,
    Long userId,
    String userName,
    String userEmail,
    BigDecimal totalAmount,
    OrderStatus status,
    LocalDateTime createdAt,
    List<OrderItemResponse> items
) {}