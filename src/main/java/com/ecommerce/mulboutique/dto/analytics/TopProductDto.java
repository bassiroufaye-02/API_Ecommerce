package com.ecommerce.mulboutique.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(example = "{\"productId\":1,\"productName\":\"Clavier Mecanique\",\"totalQuantity\":25,\"totalRevenue\":1997.50}")
public class TopProductDto {
    private Long productId;
    private String productName;
    private Long totalQuantity;
    private BigDecimal totalRevenue;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Long getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Long totalQuantity) { this.totalQuantity = totalQuantity; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
}
