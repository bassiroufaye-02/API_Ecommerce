package com.ecommerce.mulboutique.repository;

import com.ecommerce.mulboutique.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    Optional<Coupon> findByCode(String code);

    boolean existsByCode(String code);
    
    List<Coupon> findByStoreId(Long storeId);
    
    List<Coupon> findByStoreIdAndIsActive(Long storeId, Boolean isActive);
    
    @Query("SELECT c FROM Coupon c WHERE c.code = :code AND c.store.id = :storeId AND c.isActive = :isActive")
    Optional<Coupon> findByCodeAndStoreIdAndIsActive(@Param("code") String code, @Param("storeId") Long storeId, @Param("isActive") Boolean isActive);
    
    @Query("SELECT c FROM Coupon c WHERE c.store.id = :storeId AND c.isActive = true AND c.validFrom <= :currentDate AND c.validUntil >= :currentDate")
    List<Coupon> findValidCouponsByStoreId(@Param("storeId") Long storeId, @Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT c FROM Coupon c WHERE c.store.id = :storeId AND c.isActive = true AND c.validFrom <= :currentDate AND c.validUntil >= :currentDate AND (c.usageLimit IS NULL OR c.usageCount < c.usageLimit)")
    List<Coupon> findAvailableCouponsByStoreId(@Param("storeId") Long storeId, @Param("currentDate") LocalDateTime currentDate);
    
    boolean existsByCodeAndStoreId(String code, Long storeId);
    
    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.store.id = :storeId AND c.isActive = true")
    long countActiveCouponsByStoreId(@Param("storeId") Long storeId);
}
