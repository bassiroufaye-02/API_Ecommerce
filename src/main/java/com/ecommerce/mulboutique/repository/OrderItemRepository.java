package com.ecommerce.mulboutique.repository;

import com.ecommerce.mulboutique.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderId(Long orderId);
    
    List<OrderItem> findByProductId(Long productId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderWithProducts(@Param("orderId") Long orderId);
    
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.id = :productId")
    Integer getTotalQuantitySoldByProductId(@Param("productId") Long productId);
    
    @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi WHERE oi.product.id = :productId")
    Double getTotalRevenueByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.order.id = :orderId")
    long countByOrderId(@Param("orderId") Long orderId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.store.id = :storeId")
    List<OrderItem> findByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT oi.product.id, oi.product.name, SUM(oi.quantity), SUM(oi.totalPrice) " +
           "FROM OrderItem oi " +
           "WHERE oi.order.store.id = :storeId AND oi.order.orderDate BETWEEN :startDate AND :endDate " +
           "GROUP BY oi.product.id, oi.product.name " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopProductsByStoreAndDateRange(@Param("storeId") Long storeId,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);
}
