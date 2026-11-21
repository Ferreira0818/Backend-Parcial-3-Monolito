package com.poe.monolito.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
    Long id,
    Long productId,
    String productName,
    BigDecimal priceAtPurchase, // Precio congelado al momento de la compra
    Integer quantity,
    BigDecimal subtotal,
    String imageUrl
) {}