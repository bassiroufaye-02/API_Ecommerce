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
@RequestMapping("/api/v1/categories")
@Tag(name = "CatÃ©gories", description = "API pour la gestion des catÃ©gories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/store/{storeId}")
    @Operation(summary = "CatÃ©gories d'une boutique", description = "Retourne la liste des catÃ©gories d'une boutique spÃ©cifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des catÃ©gories rÃ©cupÃ©rÃ©e avec succÃ¨s"),
        @ApiResponse(responseCode = "404", description = "Boutique non trouvÃ©e")
    })
    public ResponseEntity<List<CategoryDto>> getCategoriesByStore(@PathVariable Long storeId) {
        List<CategoryDto> categories = categoryService.getCategoriesByStore(storeId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "DÃ©tails d'une catÃ©gorie", description = "Retourne les dÃ©tails d'une catÃ©gorie spÃ©cifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "CatÃ©gorie trouvÃ©e"),
        @ApiResponse(responseCode = "404", description = "CatÃ©gorie non trouvÃ©e")
    })
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "CrÃ©er une catÃ©gorie", description = "CrÃ©e une nouvelle catÃ©gorie")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "CatÃ©gorie crÃ©Ã©e avec succÃ¨s"),
        @ApiResponse(responseCode = "400", description = "DonnÃ©es invalides"),
        @ApiResponse(responseCode = "403", description = "AccÃ¨s refusÃ©")
    })
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryRequest category,
                                                     @RequestParam Long storeId) {
        CategoryDto createdCategory = categoryService.createCategory(mapToEntity(category), storeId);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Mettre Ã  jour une catÃ©gorie", description = "Met Ã  jour les informations d'une catÃ©gorie")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "CatÃ©gorie mise Ã  jour avec succÃ¨s"),
        @ApiResponse(responseCode = "404", description = "CatÃ©gorie non trouvÃ©e"),
        @ApiResponse(responseCode = "403", description = "AccÃ¨s refusÃ©")
    })
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id,
                                                    @Valid @RequestBody CategoryRequest category) {
        CategoryDto updatedCategory = categoryService.updateCategory(id, mapToEntity(category));
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Supprimer une catÃ©gorie", description = "Supprime une catÃ©gorie")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "CatÃ©gorie supprimÃ©e avec succÃ¨s"),
        @ApiResponse(responseCode = "404", description = "CatÃ©gorie non trouvÃ©e"),
        @ApiResponse(responseCode = "403", description = "AccÃ¨s refusÃ©")
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

