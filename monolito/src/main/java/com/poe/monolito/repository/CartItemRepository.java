package com.poe.monolito.repository;

import com.poe.monolito.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Buscar si un producto ya está dentro del carrito de un usuario
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}