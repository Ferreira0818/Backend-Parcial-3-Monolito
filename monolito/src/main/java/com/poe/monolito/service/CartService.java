package com.poe.monolito.service;

import com.poe.monolito.dto.AddToCartRequest;
import com.poe.monolito.dto.CartItemResponse;
import com.poe.monolito.dto.CartResponse;
import com.poe.monolito.entity.Cart;
import com.poe.monolito.entity.CartItem;
import com.poe.monolito.entity.Product;
import com.poe.monolito.entity.User;
import com.poe.monolito.repository.CartRepository;
import com.poe.monolito.repository.ProductRepository;
import com.poe.monolito.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Obtener o crear el carrito persistente del usuario
    @Transactional
    public Cart getOrCreateCartEntity(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            Cart newCart = Cart.builder().user(user).build();
            return cartRepository.save(newCart);
        });
    }

    // Consulta del carrito para React: GET /api/v1/cart/{userId}
    @Transactional(readOnly = true)
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado para el usuario"));
        return mapToCartResponse(cart);
    }

    // Agregar producto al carrito: POST /api/v1/cart/{userId}/items
    @Transactional
    public CartResponse addItemToCart(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCartEntity(userId);

        Product product = productRepository.findByIdAndActiveTrue(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("El orbe no existe o no está habilitado para la venta"));

        // 1. Verificar si el ítem ya existe en el carrito
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.productId()))
                .findFirst();

        int totalQuantityRequested = request.quantity();
        if (existingItemOpt.isPresent()) {
            totalQuantityRequested += existingItemOpt.get().getQuantity();
        }

        // 2. Validar stock disponible en el alijo
        if (totalQuantityRequested > product.getStock()) {
            throw new IllegalArgumentException("Stock insuficiente en el alijo. Stock disponible: " + product.getStock());
        }

        // 3. Actualizar cantidad o crear nuevo CartItem
        if (existingItemOpt.isPresent()) {
            existingItemOpt.get().setQuantity(totalQuantityRequested);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.quantity())
                    .build();
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    // Actualizar cantidad de un ítem: PATCH /api/v1/cart/{userId}/items/{itemId}?quantity=10
    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long itemId, Integer newQuantity) {
        if (newQuantity <= 0) {
            return removeItemFromCart(userId, itemId);
        }

        Cart cart = getOrCreateCartEntity(userId);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ítem no encontrado en el carrito"));

        if (newQuantity > item.getProduct().getStock()) {
            throw new IllegalArgumentException("Stock insuficiente. Máximo disponible: " + item.getProduct().getStock());
        }

        item.setQuantity(newQuantity);
        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    // Eliminar un ítem: DELETE /api/v1/cart/{userId}/items/{itemId}
    @Transactional
    public CartResponse removeItemFromCart(Long userId, Long itemId) {
        Cart cart = getOrCreateCartEntity(userId);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    // Vaciar todo el carrito: DELETE /api/v1/cart/{userId}
    @Transactional
    public CartResponse clearCart(Long userId) {
        Cart cart = getOrCreateCartEntity(userId);
        cart.getItems().clear();
        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    // Mapeo a DTO con cálculo de subtotales y total general en USD
    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream().map(item -> {
            BigDecimal price = item.getProduct().getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            return new CartItemResponse(
                    item.getId(),
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    price,
                    item.getQuantity(),
                    subtotal,
                    item.getProduct().getImageUrl()
            );
        }).toList();

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return new CartResponse(cart.getId(), cart.getUser().getId(), itemResponses, total);
    }
}