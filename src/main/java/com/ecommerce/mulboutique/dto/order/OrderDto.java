package com.ecommerce.mulboutique.dto.order;

import com.ecommerce.mulboutique.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(example = "{\"id\":1,\"orderNumber\":\"ORD-20260305-0001\",\"storeId\":1,\"storeName\":\"Tech Dakar\",\"totalAmount\":159.80,\"discountAmount\":10.00,\"finalAmount\":149.80,\"status\":\"PENDING\",\"paymentStatus\":\"PENDING\",\"shippingStatus\":\"PENDING\",\"shippingMethod\":\"STANDARD\",\"paymentMethod\":\"CARD\",\"paymentReference\":\"PAY-20260305-0001\",\"shippingProvider\":\"DHL\",\"trackingNumber\":\"SN123456789\",\"shippingAddress\":\"10 rue Exemple, Dakar, 11000, Senegal\",\"billingAddress\":\"10 rue Exemple, Dakar, 11000, Senegal\",\"orderDate\":\"2026-03-05T12:00:00\"}")
public class OrderDto {
    private Long id;
    private String orderNumber;
    private Long storeId;
    private String storeName;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private Order.OrderStatus status;
    private Order.PaymentStatus paymentStatus;
    private Order.ShippingStatus shippingStatus;
    private Order.ShippingMethod shippingMethod;
    private String paymentMethod;
    private String paymentReference;
    private String shippingProvider;
    private String trackingNumber;
    private String shippingAddress;
    private String billingAddress;
    private LocalDateTime orderDate;
    private List<OrderItemDto> items;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public Order.OrderStatus getStatus() { return status; }
    public void setStatus(Order.OrderStatus status) { this.status = status; }

    public Order.PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(Order.PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public Order.ShippingStatus getShippingStatus() { return shippingStatus; }
    public void setShippingStatus(Order.ShippingStatus shippingStatus) { this.shippingStatus = shippingStatus; }

    public Order.ShippingMethod getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(Order.ShippingMethod shippingMethod) { this.shippingMethod = shippingMethod; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }

    public String getShippingProvider() { return shippingProvider; }
    public void setShippingProvider(String shippingProvider) { this.shippingProvider = shippingProvider; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
}
