package com.poe.monolito.dto;

import com.poe.monolito.entity.enums.MessageStatus;

import java.time.LocalDateTime;

public record ContactResponse(
    Long id,
    String name,
    String email,
    String subject,
    String message,
    MessageStatus status,
    LocalDateTime createdAt
) {}