package com.ecommerce.mulboutique.repository;

import com.ecommerce.mulboutique.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByCustomerId(Long customerId);
    
    List<Order> findByStoreId(Long storeId);
    
    List<Order> findByCustomerIdAndStoreId(Long customerId, Long storeId);
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    List<Order> findByStoreIdAndStatus(Long storeId, Order.OrderStatus status);
    
    List<Order> findByCustomerIdAndStatus(Long customerId, Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.store.id = :storeId ORDER BY o.createdAt DESC")
    Page<Order> findByStoreIdOrderByCreatedAtDesc(@Param("storeId") Long storeId, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.createdAt DESC")
    Page<Order> findByCustomerIdOrderByCreatedAtDesc(@Param("customerId") Long customerId, Pageable pageable);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.store.id = :storeId AND o.status = :status")
    long countByStoreIdAndStatus(@Param("storeId") Long storeId, @Param("status") Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.store.id = :storeId")
    List<Order> findByStoreIdAndOrderDateBetween(@Param("storeId") Long storeId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
