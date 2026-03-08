package com.ecommerce.mulboutique.repository;

import com.ecommerce.mulboutique.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    List<Product> findByStoreId(Long storeId);
    
    List<Product> findByStoreIdAndIsActive(Long storeId, Boolean isActive);
    
    @EntityGraph(attributePaths = {"category", "store"})
    List<Product> findByStoreIdAndIsActiveTrue(Long storeId);
    
    List<Product> findByCategoryId(Long categoryId);
    
    List<Product> findByCategoryIdAndIsActive(Long categoryId, Boolean isActive);
    
    @EntityGraph(attributePaths = {"category", "store"})
    List<Product> findByCategoryIdAndIsActiveTrue(Long categoryId);

    @EntityGraph(attributePaths = {"category", "store"})
    Optional<Product> findById(Long id);

    @EntityGraph(attributePaths = {"category", "store"})
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
    
    Optional<Product> findBySku(String sku);
    
    boolean existsBySku(String sku);
    
    @Query("SELECT p FROM Product p WHERE p.store.id = :storeId AND p.name LIKE %:name% AND p.isActive = :isActive")
    List<Product> findByStoreIdAndNameContainingAndIsActive(@Param("storeId") Long storeId, @Param("name") String name, @Param("isActive") Boolean isActive);
    
    @Query("SELECT p FROM Product p WHERE p.store.id = :storeId AND p.isActive = true ORDER BY p.createdAt DESC")
    Page<Product> findActiveProductsByStoreId(@Param("storeId") Long storeId, Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.store.id = :storeId AND p.isActive = true")
    long countActiveProductsByStoreId(@Param("storeId") Long storeId);
    
    @Query("SELECT p FROM Product p WHERE p.stockQuantity < :threshold AND p.isActive = true")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);

    @Query("SELECT p FROM Product p WHERE p.store.id = :storeId AND p.stockQuantity < :threshold AND p.isActive = true")
    List<Product> findLowStockProductsByStore(@Param("storeId") Long storeId, @Param("threshold") int threshold);
}
