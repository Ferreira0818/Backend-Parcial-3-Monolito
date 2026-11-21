package com.poe.monolito.controller;

import com.poe.monolito.dto.OrderResponse;
import com.poe.monolito.dto.UpdateOrderStatusRequest;
import com.poe.monolito.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Checkout: Convierte el carrito en una orden: POST /api/v1/orders/checkout/{userId}
    @PostMapping("/checkout/{userId}")
    public ResponseEntity<OrderResponse> checkout(@PathVariable Long userId) {
        OrderResponse response = orderService.checkout(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Historial de compras de un usuario: GET /api/v1/orders/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    // Detalle de orden por código: GET /api/v1/orders/{orderNumber}
    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrderByNumber(orderNumber));
    }

    // Listado general para Admin: GET /api/v1/orders
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Actualizar estado de la orden (Admin): PATCH /api/v1/orders/{orderId}/status
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request));
    }
}