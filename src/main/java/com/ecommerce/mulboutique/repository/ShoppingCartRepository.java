package com.ecommerce.mulboutique.repository;

import com.ecommerce.mulboutique.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    
    Optional<ShoppingCart> findByCustomerId(Long customerId);
    
    Optional<ShoppingCart> findByCustomerIdAndStoreId(Long customerId, Long storeId);
    
    List<ShoppingCart> findByStoreId(Long storeId);
    
    @Query("SELECT sc FROM ShoppingCart sc WHERE sc.customer.id = :customerId AND sc.store.id = :storeId")
    Optional<ShoppingCart> findByCustomerAndStore(@Param("customerId") Long customerId, @Param("storeId") Long storeId);
    
    @Query("SELECT COUNT(sc) FROM ShoppingCart sc WHERE sc.store.id = :storeId")
    long countByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT COUNT(sc) FROM ShoppingCart sc WHERE sc.store.id = :storeId AND sc.createdAt BETWEEN :startDate AND :endDate")
    long countByStoreIdAndCreatedAtBetween(@Param("storeId") Long storeId,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    
    boolean existsByCustomerIdAndStoreId(Long customerId, Long storeId);
}
