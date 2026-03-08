package com.ecommerce.mulboutique.controller;

import com.ecommerce.mulboutique.dto.coupon.CouponDto;
import com.ecommerce.mulboutique.dto.coupon.CreateCouponRequest;
import com.ecommerce.mulboutique.entity.Coupon;
import com.ecommerce.mulboutique.entity.Store;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.exception.ForbiddenException;
import com.ecommerce.mulboutique.exception.NotFoundException;
import com.ecommerce.mulboutique.repository.StoreRepository;
import com.ecommerce.mulboutique.service.CouponService;
import com.ecommerce.mulboutique.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Tag(name = "Coupons", description = "Gestion des coupons et promotions")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private StoreRepository storeRepository;

    @PostMapping("/api/store-owners/coupons")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Créer un coupon")
    public ResponseEntity<CouponDto> createCoupon(@Valid @RequestBody CreateCouponRequest request, @RequestParam Long storeId) {
        ensureStoreAccess(storeId);
        CouponDto coupon = couponService.createCoupon(request, storeId);
        return new ResponseEntity<>(coupon, HttpStatus.CREATED);
    }

    @PutMapping("/api/store-owners/coupons/{id}")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour un coupon")
    public ResponseEntity<CouponDto> updateCoupon(@PathVariable Long id, @Valid @RequestBody CreateCouponRequest request) {
        return ResponseEntity.ok(couponService.updateCoupon(id, request));
    }

    @DeleteMapping("/api/store-owners/coupons/{id}")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Désactiver un coupon")
    public ResponseEntity<Void> deactivateCoupon(@PathVariable Long id) {
        couponService.deactivateCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/store-owners/coupons")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Lister les coupons d'une boutique")
    public ResponseEntity<List<CouponDto>> getCouponsByStore(@RequestParam Long storeId) {
        ensureStoreAccess(storeId);
        return ResponseEntity.ok(couponService.getCouponsByStore(storeId));
    }

    @GetMapping("/api/coupons/validate")
    @Operation(summary = "Valider un coupon")
    public ResponseEntity<String> validateCoupon(@RequestParam String code, @RequestParam Long storeId, @RequestParam(required = false) BigDecimal cartTotal) {
        Coupon coupon = couponService.validateCoupon(code, storeId, cartTotal);
        return ResponseEntity.ok("Coupon valide: " + coupon.getCode());
    }

    private void ensureStoreAccess(Long storeId) {
        User user = currentUserService.getCurrentUser();
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("Boutique non trouvee"));
        if (!store.getOwner().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new ForbiddenException("Acces refuse");
        }
    }
}
