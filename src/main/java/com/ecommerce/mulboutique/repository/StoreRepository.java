package com.ecommerce.mulboutique.repository;

import com.ecommerce.mulboutique.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    
    List<Store> findByOwnerId(Long ownerId);
    
    List<Store> findByOwnerIdAndIsActiveTrue(Long ownerId);
    
    Optional<Store> findByOwnerIdAndIsActive(Long ownerId, Boolean isActive);
    
    List<Store> findByIsActive(Boolean isActive);
    
    List<Store> findByIsActiveTrue();

    @Query("SELECT s FROM Store s JOIN FETCH s.owner WHERE s.isActive = true")
    List<Store> findActiveWithOwner();

    @Query("SELECT s FROM Store s JOIN FETCH s.owner WHERE s.id = :id")
    Optional<Store> findByIdWithOwner(@Param("id") Long id);

    @Query("SELECT s FROM Store s JOIN FETCH s.owner WHERE s.id = :id AND s.isActive = true")
    Optional<Store> findActiveByIdWithOwner(@Param("id") Long id);

    @Query("SELECT s FROM Store s JOIN FETCH s.owner WHERE s.owner.id = :ownerId AND s.isActive = true")
    List<Store> findActiveByOwnerIdWithOwner(@Param("ownerId") Long ownerId);
    
    @Query("SELECT s FROM Store s WHERE s.name LIKE %:name% AND s.isActive = :isActive")
    List<Store> findByNameContainingAndIsActive(@Param("name") String name, @Param("isActive") Boolean isActive);
    
    @Query("SELECT COUNT(s) FROM Store s WHERE s.owner.id = :ownerId AND s.isActive = true")
    long countActiveStoresByOwner(@Param("ownerId") Long ownerId);
    
    boolean existsByNameAndOwnerId(String name, Long ownerId);
}
