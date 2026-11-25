package com.poe.monolito.service;

import com.poe.monolito.dto.OrderItemResponse;
import com.poe.monolito.dto.OrderResponse;
import com.poe.monolito.dto.UpdateOrderStatusRequest;
import com.poe.monolito.entity.*;
import com.poe.monolito.entity.enums.OrderStatus;
import com.poe.monolito.repository.CartRepository;
import com.poe.monolito.repository.OrderRepository;
import com.poe.monolito.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    // CHECKOUT TRANSACCIONAL: Convierte el carrito en una orden formal
    @Transactional
    public OrderResponse checkout(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío. Agrega orbes antes de comprar.");
        }

        User user = cart.getUser();
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 1. Crear la cabecera de la orden
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .status(OrderStatus.PENDING) // Estado inicial: esperando intercambio in-game
                .createdAt(LocalDateTime.now())
                .build();

        // 2. Procesar cada orbe del carrito
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int requestedQuantity = cartItem.getQuantity();

            if (!Boolean.TRUE.equals(product.getActive())) {
                throw new IllegalArgumentException("El orbe ya no está disponible para la venta: " + product.getName());
            }

            // Validación estricta de stock disponible en el alijo
            if (product.getStock() < requestedQuantity) {
                throw new IllegalArgumentException("Stock insuficiente para: " + product.getName() 
                        + ". Disponible: " + product.getStock());
            }

            // Descontar inventario del producto
            product.setStock(product.getStock() - requestedQuantity);
            productRepository.save(product);

            // Congelar el precio actual para el historial contable
            BigDecimal priceAtPurchase = product.getPrice();
            BigDecimal subtotal = priceAtPurchase.multiply(BigDecimal.valueOf(requestedQuantity));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(requestedQuantity)
                    .priceAtPurchase(priceAtPurchase)
                    .build();

            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
        order.setItems(orderItems);

        // 3. Persistir la orden y sus items (CascadeType.ALL)
        Order savedOrder = orderRepository.save(order);

        // 4. Vaciar el carrito de compras del usuario
        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToOrderResponse(savedOrder);
    }

    // Historial del usuario autenticado: GET /api/v1/orders/user/{userId}
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    // Detalle de orden por código: GET /api/v1/orders/{orderNumber}
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + orderNumber));
        return mapToOrderResponse(order);
    }

    // Listado general para administración: GET /api/v1/orders
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    // Actualizar estado de la orden (Admin): PATCH /api/v1/orders/{orderId}/status
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));

        // Buena práctica: Si la orden se cancela, reponer el stock a los productos
        if (request.status() == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(request.status());
        Order updated = orderRepository.save(order);
        return mapToOrderResponse(updated);
    }

    // Generador de códigos tipo: ORD-2026-X8F1
    private String generateOrderNumber() {
        String randomSuffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "ORD-" + LocalDateTime.now().getYear() + "-" + randomSuffix;
    }

    // Mapeo a DTO de respuesta
    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream().map(item -> {
            BigDecimal subtotal = item.getPriceAtPurchase()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            return new OrderItemResponse(
                    item.getId(),
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getPriceAtPurchase(),
                    item.getQuantity(),
                    subtotal,
                    item.getProduct().getImageUrl()
            );
        }).toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getUser().getEmail(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}