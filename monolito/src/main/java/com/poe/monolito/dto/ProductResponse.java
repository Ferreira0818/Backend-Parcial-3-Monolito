package com.poe.monolito.dto;

import java.math.BigDecimal;

public record ProductResponse(
    Long id,
    String name,
    BigDecimal price,
    String imageUrl,
    String altText,
    Integer stock,
    Boolean active
) {}