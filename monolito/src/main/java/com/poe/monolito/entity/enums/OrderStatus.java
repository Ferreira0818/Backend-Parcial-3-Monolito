package com.poe.monolito.entity.enums;

public enum OrderStatus {
    PENDING,    // Esperando el intercambio (trade) in-game en Wraeclast
    COMPLETED,  // Orbes entregados al jugador
    CANCELLED   // Cancelada o stock insuficiente
}
