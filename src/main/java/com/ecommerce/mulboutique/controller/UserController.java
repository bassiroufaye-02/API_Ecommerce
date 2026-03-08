package com.ecommerce.mulboutique.controller;

import com.ecommerce.mulboutique.dto.user.AddressDto;
import com.ecommerce.mulboutique.dto.user.AddressRequest;
import com.ecommerce.mulboutique.dto.user.PaymentMethodDto;
import com.ecommerce.mulboutique.dto.user.PaymentMethodRequest;
import com.ecommerce.mulboutique.dto.user.UpdateProfileRequest;
import com.ecommerce.mulboutique.dto.user.UserProfileDto;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.service.CurrentUserService;
import com.ecommerce.mulboutique.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Utilisateurs", description = "Profil, adresses, paiements")
public class UserController {

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Voir mon profil")
    public ResponseEntity<UserProfileDto> getProfile() {
        User user = currentUserService.getCurrentUser();
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/me")
    @Operation(summary = "Mettre Ã  jour mon profil")
    public ResponseEntity<UserProfileDto> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User user = currentUserService.getCurrentUser();
        User updated = userService.updateProfile(user, request);
        UserProfileDto dto = new UserProfileDto();
        dto.setId(updated.getId());
        dto.setUsername(updated.getUsername());
        dto.setEmail(updated.getEmail());
        dto.setFirstName(updated.getFirstName());
        dto.setLastName(updated.getLastName());
        dto.setPhone(updated.getPhone());
        dto.setRole(updated.getRole());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me/addresses")
    @Operation(summary = "Lister mes adresses")
    public ResponseEntity<List<AddressDto>> getAddresses() {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(userService.getAddresses(user));
    }

    @PostMapping("/me/addresses")
    @Operation(summary = "Ajouter une adresse")
    public ResponseEntity<AddressDto> addAddress(@Valid @RequestBody AddressRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(userService.addAddress(user, request));
    }

    @PutMapping("/me/addresses/{addressId}")
    @Operation(summary = "Mettre Ã  jour une adresse")
    public ResponseEntity<AddressDto> updateAddress(@PathVariable Long addressId, @Valid @RequestBody AddressRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(userService.updateAddress(addressId, user, request));
    }

    @DeleteMapping("/me/addresses/{addressId}")
    @Operation(summary = "Supprimer une adresse")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        User user = currentUserService.getCurrentUser();
        userService.deleteAddress(addressId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/payment-methods")
    @Operation(summary = "Lister mes moyens de paiement")
    public ResponseEntity<List<PaymentMethodDto>> getPaymentMethods() {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(userService.getPaymentMethods(user));
    }

    @PostMapping("/me/payment-methods")
    @Operation(summary = "Ajouter un moyen de paiement")
    public ResponseEntity<PaymentMethodDto> addPaymentMethod(@Valid @RequestBody PaymentMethodRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(userService.addPaymentMethod(user, request));
    }

    @PutMapping("/me/payment-methods/{id}")
    @Operation(summary = "Mettre Ã  jour un moyen de paiement")
    public ResponseEntity<PaymentMethodDto> updatePaymentMethod(@PathVariable Long id, @Valid @RequestBody PaymentMethodRequest request) {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(userService.updatePaymentMethod(id, user, request));
    }

    @DeleteMapping("/me/payment-methods/{id}")
    @Operation(summary = "Supprimer un moyen de paiement")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long id) {
        User user = currentUserService.getCurrentUser();
        userService.deletePaymentMethod(id, user);
        return ResponseEntity.noContent().build();
    }
}

