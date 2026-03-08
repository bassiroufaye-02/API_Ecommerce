package com.ecommerce.mulboutique.controller;

import com.ecommerce.mulboutique.dto.ProductDto;
import com.ecommerce.mulboutique.entity.Store;
import com.ecommerce.mulboutique.entity.User;
import com.ecommerce.mulboutique.exception.ForbiddenException;
import com.ecommerce.mulboutique.exception.NotFoundException;
import com.ecommerce.mulboutique.repository.StoreRepository;
import com.ecommerce.mulboutique.service.CurrentUserService;
import com.ecommerce.mulboutique.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/store-owners/stock")
@Tag(name = "Stocks", description = "Suivi des stocks")
public class StockController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private StoreRepository storeRepository;

    @Value("${stock.low-threshold:5}")
    private int lowStockThreshold;

    @GetMapping("/low")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Produits Ã  stock faible")
    public ResponseEntity<List<ProductDto>> getLowStock(@RequestParam Long storeId) {
        User user = currentUserService.getCurrentUser();
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("Boutique non trouvee"));
        if (!store.getOwner().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new ForbiddenException("Acces refuse");
        }
        return ResponseEntity.ok(productService.getLowStockProducts(storeId, lowStockThreshold));
    }
}

