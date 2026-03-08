package com.ecommerce.mulboutique.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(example = "{\"id\":1,\"storeId\":1,\"storeName\":\"Tech Dakar\",\"totalAmount\":159.80,\"itemCount\":2,\"items\":[{\"id\":1,\"productId\":1,\"productName\":\"Clavier Mecanique\",\"quantity\":2,\"unitPrice\":79.90,\"totalPrice\":159.80}],\"updatedAt\":\"2026-03-05T11:00:00\"}")
public class CartDto {
    private Long id;
    private Long storeId;
    private String storeName;
    private BigDecimal totalAmount;
    private Integer itemCount;
    private List<CartItemDto> items;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public Integer getItemCount() { return itemCount; }
    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }

    public List<CartItemDto> getItems() { return items; }
    public void setItems(List<CartItemDto> items) { this.items = items; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
