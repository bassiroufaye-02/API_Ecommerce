package com.ecommerce.mulboutique.controller;

import com.ecommerce.mulboutique.dto.order.CreateOrderRequest;
import com.ecommerce.mulboutique.dto.order.OrderDto;
import com.ecommerce.mulboutique.dto.order.UpdateOrderStatusRequest;
import com.ecommerce.mulboutique.dto.order.UpdateShippingRequest;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.service.CurrentUserService;
import com.ecommerce.mulboutique.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Commandes", description = "Gestion des commandes")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CurrentUserService currentUserService;

    @PostMapping("/api/v1/clients/orders")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Passer une commande")
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        User user = currentUserService.getCurrentUser();
        OrderDto order = orderService.createOrder(request, user);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/api/v1/clients/orders")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Lister mes commandes")
    public ResponseEntity<List<OrderDto>> getMyOrders() {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(orderService.getOrdersByCustomer(user));
    }

    @GetMapping("/api/v1/store-owners/orders")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Lister les commandes d'une boutique")
    public ResponseEntity<List<OrderDto>> getOrdersByStore(@RequestParam Long storeId) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(orderService.getOrdersByStoreForOwner(storeId, user));
    }

    @PutMapping("/api/v1/store-owners/orders/{orderId}/status")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Mettre Ã  jour le statut d'une commande")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long orderId, @Valid @RequestBody UpdateOrderStatusRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request, user));
    }

    @PutMapping("/api/v1/store-owners/orders/{orderId}/shipping")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Mettre Ã  jour l'expÃ©dition")
    public ResponseEntity<OrderDto> updateShipping(@PathVariable Long orderId, @Valid @RequestBody UpdateShippingRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(orderService.updateShipping(orderId, request, user));
    }
}

