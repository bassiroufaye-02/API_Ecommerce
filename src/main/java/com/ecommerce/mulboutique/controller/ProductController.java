package com.ecommerce.mulboutique.controller;

import com.ecommerce.mulboutique.dto.ProductDto;
import com.ecommerce.mulboutique.dto.ProductRequest;
import com.ecommerce.mulboutique.entity.Product;
import com.ecommerce.mulboutique.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Produits", description = "API pour la gestion des produits")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/store/{storeId}")
    @Operation(summary = "Produits d'une boutique", description = "Retourne la liste des produits d'une boutique spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des produits récupérée avec succès"),
        @ApiResponse(responseCode = "404", description = "Boutique non trouvée")
    })
    public ResponseEntity<List<ProductDto>> getProductsByStore(@PathVariable Long storeId) {
        List<ProductDto> products = productService.getProductsByStore(storeId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'un produit", description = "Retourne les détails d'un produit spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produit trouvé"),
        @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Produits par catégorie", description = "Retourne la liste des produits d'une catégorie spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des produits récupérée avec succès")
    })
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductDto> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    @Operation(summary = "Recherche avancée", description = "Recherche multicritaire avec pagination et tri")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Résultats de recherche")
    })
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<ProductDto> results = productService.searchProducts(q, minPrice, maxPrice, categoryId, storeId, inStock, pageable);
        return ResponseEntity.ok(results);
    }

    @PostMapping
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Créer un produit", description = "Crée un nouveau produit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Produit créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductRequest product,
                                                  @RequestParam Long storeId,
                                                  @RequestParam Long categoryId) {
        ProductDto createdProduct = productService.createProduct(mapToEntity(product), storeId, categoryId);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour un produit", description = "Met à jour les informations d'un produit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produit mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id,
                                                   @Valid @RequestBody ProductRequest product) {
        ProductDto updatedProduct = productService.updateProduct(id, mapToEntity(product));
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Supprimer un produit", description = "Désactive un produit (suppression logique)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Produit supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    private Product mapToEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        String imageUrl = request.getImageUrl();
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            imageUrl = "/api/uploads/UPLOAD.jpg";
        }
        product.setImageUrl(imageUrl);
        product.setSku(request.getSku());
        if (request.getActive() != null) {
            product.setIsActive(request.getActive());
        }
        return product;
    }
}
