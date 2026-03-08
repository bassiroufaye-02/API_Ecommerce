package com.ecommerce.mulboutique.controller;

import com.ecommerce.mulboutique.dto.payment.PaymentRequest;
import com.ecommerce.mulboutique.dto.payment.PaymentResponse;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.service.CurrentUserService;
import com.ecommerce.mulboutique.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Paiements", description = "Simulation de paiement")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CurrentUserService currentUserService;

    @PostMapping("/initiate")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    @Operation(summary = "Initier un paiement")
    public ResponseEntity<PaymentResponse> initiate(@Valid @RequestBody PaymentRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(paymentService.initiatePayment(request.getOrderId(), request.getPaymentMethod(), user));
    }

    @PostMapping("/confirm")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    @Operation(summary = "Confirmer un paiement")
    public ResponseEntity<PaymentResponse> confirm(@Valid @RequestBody PaymentRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(paymentService.confirmPayment(request.getOrderId(), user));
    }

    @PostMapping("/fail")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    @Operation(summary = "Paiement échoué")
    public ResponseEntity<PaymentResponse> fail(@Valid @RequestBody PaymentRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(paymentService.failPayment(request.getOrderId(), user));
    }

    @PostMapping("/refund")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    @Operation(summary = "Rembourser un paiement")
    public ResponseEntity<PaymentResponse> refund(@Valid @RequestBody PaymentRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(paymentService.refundPayment(request.getOrderId(), user));
    }
}
