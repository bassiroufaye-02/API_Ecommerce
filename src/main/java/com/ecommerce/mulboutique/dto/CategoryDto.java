package com.ecommerce.mulboutique.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(example = "{\"id\":1,\"name\":\"Informatique\",\"description\":\"Produits informatiques\",\"storeId\":1,\"storeName\":\"Tech Dakar\"}")
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
    private Long storeId;
    private String storeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CategoryDto() {}

    public CategoryDto(Long id, String name, String description, Long storeId, String storeName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.storeId = storeId;
        this.storeName = storeName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
