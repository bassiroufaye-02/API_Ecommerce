package com.ecommerce.mulboutique.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(example = "{\"name\":\"Clavier Mecanique\",\"description\":\"Clavier gaming\",\"price\":79.90,\"stockQuantity\":50,\"imageUrl\":\"./image/clavier.png\",\"sku\":\"KB-001\",\"active\":true}")
public class ProductRequest {
    @NotBlank
    @Size(max = 150)
    private String name;

    @Size(max = 2000)
    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @NotNull
    private Integer stockQuantity;

    @Size(max = 255)
    private String imageUrl;

    @Size(max = 50)
    private String sku;

    private Boolean active;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
