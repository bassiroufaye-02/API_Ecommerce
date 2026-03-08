package com.ecommerce.mulboutique.repository;

import com.ecommerce.mulboutique.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByProductId(Long productId);
    
    List<Review> findByCustomerId(Long customerId);
    
    Optional<Review> findByProductIdAndCustomerId(Long productId, Long customerId);
    
    List<Review> findByProductIdAndIsVerified(Long productId, Boolean isVerified);
    
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId ORDER BY r.createdAt DESC")
    Page<Review> findByProductIdOrderByCreatedAtDesc(@Param("productId") Long productId, Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.isVerified = true")
    Double getAverageRatingByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.isVerified = true")
    long countVerifiedReviewsByProductId(@Param("productId") Long productId);
    
    @Query("SELECT r FROM Review r WHERE r.product.store.id = :storeId AND r.isVerified = false")
    List<Review> findUnverifiedReviewsByStoreId(@Param("storeId") Long storeId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.customer.id = :customerId")
    long countByCustomerId(@Param("customerId") Long customerId);
}
