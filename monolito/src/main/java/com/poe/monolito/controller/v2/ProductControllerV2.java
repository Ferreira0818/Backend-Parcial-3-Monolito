package com.poe.monolito.controller.v2;

import com.poe.monolito.controller.ProductController;
import com.poe.monolito.dto.ProductResponse;
import com.poe.monolito.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v2/products")
@RequiredArgsConstructor
public class ProductControllerV2 {

    private final ProductService productService;

    // GET /api/v2/products (Listado de orbes enriquecido con hipermedios)
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllActiveProducts();

        // Envolvemos cada orbe en un EntityModel con sus propios links
        List<EntityModel<ProductResponse>> productModels = products.stream()
                .map(this::toModel)
                .toList();

        // Creamos la colección con un link 'self' a la lista completa
        CollectionModel<EntityModel<ProductResponse>> collectionModel = CollectionModel.of(
                productModels,
                linkTo(methodOn(ProductControllerV2.class).getAllProducts()).withSelfRel()
        );

        return ResponseEntity.ok(collectionModel);
    }

    // GET /api/v2/products/{id} (Detalle de un orbe con enlaces de acción)
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProductResponse>> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(toModel(product));
    }

    // Método auxiliar para construir los enlaces HATEOAS usando WebMvcLinkBuilder
    private EntityModel<ProductResponse> toModel(ProductResponse product) {
        return EntityModel.of(
                product,
                // Enlace a sí mismo en v2: /api/v2/products/{id}
                linkTo(methodOn(ProductControllerV2.class).getProductById(product.id())).withSelfRel(),
                // Enlace a la colección: /api/v2/products
                linkTo(methodOn(ProductControllerV2.class).getAllProducts()).withRel("all-products"),
                // Enlace a la acción de modificar stock en el controlador v1
                linkTo(methodOn(ProductController.class).updateStock(product.id(), null)).withRel("update-stock")
        );
    }
}