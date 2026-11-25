package com.poe.monolito.repository;

import com.poe.monolito.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Buscar si un producto ya está dentro del carrito de un usuario
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    // Eliminar referencias en carritos activos cuando un producto es desactivado
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}
