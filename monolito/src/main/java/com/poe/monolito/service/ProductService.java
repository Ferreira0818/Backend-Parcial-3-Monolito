package com.poe.monolito.service;

import com.poe.monolito.dto.ProductRequest;
import com.poe.monolito.dto.ProductResponse;
import com.poe.monolito.entity.Product;
import com.poe.monolito.repository.CartItemRepository;
import com.poe.monolito.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    // Obtener catálogo público para la tienda en React
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllActiveProducts() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Detalle de un orbe específico
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Orbe no encontrado o deshabilitado para la venta"));

        return mapToResponse(product);
    }

    // Crear un nuevo orbe (Admin)
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Ya existe un orbe registrado con ese nombre");
        }

        Product product = Product.builder()
                .name(request.name())
                .price(request.price())
                .imageUrl(request.imageUrl())
                .altText(request.altText())
                .stock(request.stock())
                .active(request.active() != null ? request.active() : true)
                .build();

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    // Editar datos completos de un orbe (Admin)
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orbe no encontrado"));

        if (productRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new IllegalArgumentException("Ya existe otro orbe registrado con ese nombre");
        }

        product.setName(request.name());
        product.setPrice(request.price());
        product.setImageUrl(request.imageUrl());
        product.setAltText(request.altText());
        product.setStock(request.stock());
        if (request.active() != null) {
            product.setActive(request.active());
        }

        Product updated = productRepository.save(product);

        if (Boolean.FALSE.equals(updated.getActive())) {
            cartItemRepository.deleteByProductId(id);
        }

        return mapToResponse(updated);
    }

    // Eliminar (Soft Delete) un orbe del catálogo (Admin)
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Orbe no encontrado o ya deshabilitado"));

        product.setActive(false);
        productRepository.save(product);
        cartItemRepository.deleteByProductId(id);
    }

    // Actualizar stock de un orbe
    @Transactional
    public ProductResponse updateStock(Long id, Integer newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orbe no encontrado"));

        product.setStock(newStock);
        Product updated = productRepository.save(product);
        return mapToResponse(updated);
    }

    // Mapper auxiliar de Entidad -> DTO
    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.getAltText(),
                product.getStock(),
                product.getActive()
        );
    }
}
