package com.ecommerce.mulboutique.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Schema(example = "{\"storeId\":1,\"totalRevenue\":1250.50,\"revenueByPeriod\":{\"daily\":120.00,\"weekly\":450.00,\"monthly\":1250.50,\"yearly\":1250.50},\"ordersByPeriod\":{\"daily\":2,\"weekly\":6,\"monthly\":12,\"yearly\":12},\"topProducts\":[{\"productId\":1,\"productName\":\"Clavier Mecanique\",\"totalQuantity\":25,\"totalRevenue\":1997.50}],\"conversionRate\":0.25}")
public class AnalyticsResponse {
    private Long storeId;
    private BigDecimal totalRevenue;
    private Map<String, BigDecimal> revenueByPeriod;
    private Map<String, Long> ordersByPeriod;
    private List<TopProductDto> topProducts;
    private Double conversionRate;

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public Map<String, BigDecimal> getRevenueByPeriod() { return revenueByPeriod; }
    public void setRevenueByPeriod(Map<String, BigDecimal> revenueByPeriod) { this.revenueByPeriod = revenueByPeriod; }

    public Map<String, Long> getOrdersByPeriod() { return ordersByPeriod; }
    public void setOrdersByPeriod(Map<String, Long> ordersByPeriod) { this.ordersByPeriod = ordersByPeriod; }

    public List<TopProductDto> getTopProducts() { return topProducts; }
    public void setTopProducts(List<TopProductDto> topProducts) { this.topProducts = topProducts; }

    public Double getConversionRate() { return conversionRate; }
    public void setConversionRate(Double conversionRate) { this.conversionRate = conversionRate; }
}
