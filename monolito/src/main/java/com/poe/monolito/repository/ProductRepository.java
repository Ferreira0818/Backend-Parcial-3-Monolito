package com.poe.monolito.repository;

import com.poe.monolito.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Para la tienda pública de React: solo mostrar productos activos y habilitados
    List<Product> findByActiveTrue();

    // Buscar un orbe específico activo
    Optional<Product> findByIdAndActiveTrue(Long id);

    // Comprobar existencia por nombre
    boolean existsByName(String name);
}