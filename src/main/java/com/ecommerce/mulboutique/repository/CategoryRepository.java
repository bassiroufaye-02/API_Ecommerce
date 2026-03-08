package com.ecommerce.mulboutique.repository;

import com.ecommerce.mulboutique.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    @EntityGraph(attributePaths = {"store"})
    List<Category> findByStoreId(Long storeId);

    @EntityGraph(attributePaths = {"store"})
    Optional<Category> findById(Long id);
    
    List<Category> findByParentId(Long parentId);
    
    List<Category> findByStoreIdAndParentIdIsNull(Long storeId);
    
    Optional<Category> findByStoreIdAndName(Long storeId, String name);
    
    boolean existsByStoreIdAndName(Long storeId, String name);
    
    @Query("SELECT c FROM Category c WHERE c.store.id = :storeId AND c.parent IS NULL ORDER BY c.name")
    List<Category> findRootCategoriesByStoreId(@Param("storeId") Long storeId);
    
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId ORDER BY c.name")
    List<Category> findChildCategories(@Param("parentId") Long parentId);
    
    @Query("SELECT COUNT(c) FROM Category c WHERE c.store.id = :storeId")
    long countByStoreId(@Param("storeId") Long storeId);
}
