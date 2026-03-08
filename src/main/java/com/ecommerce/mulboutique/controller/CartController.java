package com.ecommerce.mulboutique.controller;

import com.ecommerce.mulboutique.dto.cart.AddToCartRequest;
import com.ecommerce.mulboutique.dto.cart.CartDto;
import com.ecommerce.mulboutique.dto.cart.UpdateCartItemRequest;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.service.CartService;
import com.ecommerce.mulboutique.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clients/cart")
@Tag(name = "Panier", description = "Gestion du panier client")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CurrentUserService currentUserService;

    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Voir le panier")
    public ResponseEntity<CartDto> getCart(@RequestParam Long storeId) {
        User user = currentUserService.getCurrentUser();
        CartDto cart = cartService.getCart(storeId, user);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Ajouter un produit au panier")
    public ResponseEntity<CartDto> addItem(@Valid @RequestBody AddToCartRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(cartService.addItem(request, user));
    }

    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Mettre Ã  jour un item du panier")
    public ResponseEntity<CartDto> updateItem(@PathVariable Long itemId, @Valid @RequestBody UpdateCartItemRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(cartService.updateItem(itemId, request.getQuantity(), user));
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Supprimer un item du panier")
    public ResponseEntity<CartDto> removeItem(@PathVariable Long itemId) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(cartService.removeItem(itemId, user));
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Vider le panier")
    public ResponseEntity<CartDto> clearCart(@RequestParam Long storeId) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(cartService.clearCart(storeId, user));
    }
}

