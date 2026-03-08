package com.ecommerce.mulboutique.controller;

import com.ecommerce.mulboutique.dto.StoreDto;
import com.ecommerce.mulboutique.dto.StoreRequest;
import com.ecommerce.mulboutique.entity.Store;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.exception.ForbiddenException;
import com.ecommerce.mulboutique.exception.NotFoundException;
import com.ecommerce.mulboutique.service.CurrentUserService;
import com.ecommerce.mulboutique.service.StoreService;
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
@RequestMapping("/api/stores")
@Tag(name = "Boutiques", description = "API pour la gestion des boutiques")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private CurrentUserService currentUserService;

    @GetMapping
    @Operation(summary = "Lister toutes les boutiques actives", description = "Retourne la liste de toutes les boutiques actives")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des boutiques récupérée avec succès")
    })
    public ResponseEntity<List<StoreDto>> getAllActiveStores() {
        List<StoreDto> stores = storeService.getAllActiveStores();
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'une boutique", description = "Retourne les détails d'une boutique spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Boutique trouvée"),
        @ApiResponse(responseCode = "404", description = "Boutique non trouvée")
    })
    public ResponseEntity<StoreDto> getStoreById(@PathVariable Long id) {
        return storeService.getStoreById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Créer une boutique", description = "Crée une nouvelle boutique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Boutique créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<StoreDto> createStore(@Valid @RequestBody StoreRequest store,
                                             @RequestParam Long ownerId) {
        User currentUser = currentUserService.getCurrentUser();
        if (currentUser.getRole() == User.Role.STORE_OWNER && !currentUser.getId().equals(ownerId)) {
            throw new ForbiddenException("Acces refuse");
        }
        StoreDto createdStore = storeService.createStore(mapToEntity(store), ownerId);
        return new ResponseEntity<>(createdStore, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour une boutique", description = "Met à jour les informations d'une boutique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Boutique mise à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Boutique non trouvée"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<StoreDto> updateStore(@PathVariable Long id,
                                             @Valid @RequestBody StoreRequest store) {
        ensureOwnerOrAdmin(id);
        StoreDto updatedStore = storeService.updateStore(id, mapToEntity(store));
        return ResponseEntity.ok(updatedStore);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Supprimer une boutique", description = "Désactive une boutique (suppression logique)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Boutique supprimée avec succès"),
        @ApiResponse(responseCode = "404", description = "Boutique non trouvée"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        ensureOwnerOrAdmin(id);
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-stores")
    @PreAuthorize("hasRole('STORE_OWNER')")
    @Operation(summary = "Mes boutiques", description = "Retourne les boutiques du propriétaire connecté")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des boutiques récupérée avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<StoreDto>> getMyStores() {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(storeService.getStoresByOwner(user.getId()));
    }

    private void ensureOwnerOrAdmin(Long storeId) {
        User user = currentUserService.getCurrentUser();
        if (user.getRole() == User.Role.ADMIN) {
            return;
        }
        StoreDto store = storeService.getStoreById(storeId)
                .orElseThrow(() -> new NotFoundException("Boutique non trouvÃ©e"));
        if (!store.getOwnerId().equals(user.getId())) {
            throw new ForbiddenException("Acces refuse");
        }
    }

    private Store mapToEntity(StoreRequest request) {
        Store store = new Store();
        store.setName(request.getName());
        store.setDescription(request.getDescription());
        store.setLogoUrl(request.getLogoUrl());
        store.setContactEmail(request.getContactEmail());
        store.setContactPhone(request.getContactPhone());
        store.setAddress(request.getAddress());
        store.setCity(request.getCity());
        store.setCountry(request.getCountry());
        store.setPostalCode(request.getPostalCode());
        return store;
    }
}
