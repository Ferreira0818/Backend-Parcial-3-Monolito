package com.poe.monolito.dto;

import com.poe.monolito.entity.enums.Role;

public record AuthResponse(
    Long id,
    String name,
    String email,
    Role role
) {}