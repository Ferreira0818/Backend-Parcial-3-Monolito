package com.poe.monolito.repository;

import com.poe.monolito.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Buscar una orden por su código único visible (ej: "ORD-2026-X8F1")
    Optional<Order> findByOrderNumber(String orderNumber);

    // Historial de compras de un usuario ordenado de más reciente a más antiguo
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Listado completo de órdenes para el panel de administración
    List<Order> findAllByOrderByCreatedAtDesc();
}