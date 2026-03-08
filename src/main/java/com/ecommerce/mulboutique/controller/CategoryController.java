package com.ecommerce.mulboutique.controller;

import com.ecommerce.mulboutique.dto.CategoryDto;
import com.ecommerce.mulboutique.dto.CategoryRequest;
import com.ecommerce.mulboutique.entity.Category;
import com.ecommerce.mulboutique.service.CategoryService;
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

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Catégories", description = "API pour la gestion des catégories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/store/{storeId}")
    @Operation(summary = "Catégories d'une boutique", description = "Retourne la liste des catégories d'une boutique spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des catégories récupérée avec succès"),
        @ApiResponse(responseCode = "404", description = "Boutique non trouvée")
    })
    public ResponseEntity<List<CategoryDto>> getCategoriesByStore(@PathVariable Long storeId) {
        List<CategoryDto> categories = categoryService.getCategoriesByStore(storeId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'une catégorie", description = "Retourne les détails d'une catégorie spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catégorie trouvée"),
        @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Créer une catégorie", description = "Crée une nouvelle catégorie")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Catégorie créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryRequest category,
                                                     @RequestParam Long storeId) {
        CategoryDto createdCategory = categoryService.createCategory(mapToEntity(category), storeId);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour une catégorie", description = "Met à jour les informations d'une catégorie")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catégorie mise à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Catégorie non trouvée"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id,
                                                    @Valid @RequestBody CategoryRequest category) {
        CategoryDto updatedCategory = categoryService.updateCategory(id, mapToEntity(category));
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Supprimer une catégorie", description = "Supprime une catégorie")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Catégorie supprimée avec succès"),
        @ApiResponse(responseCode = "404", description = "Catégorie non trouvée"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    private Category mapToEntity(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return category;
    }
}
