package com.ecommerce.mulboutique.service;

import com.ecommerce.mulboutique.dto.analytics.AnalyticsResponse;
import com.ecommerce.mulboutique.dto.analytics.TopProductDto;
import com.ecommerce.mulboutique.entity.Order;
import com.ecommerce.mulboutique.repository.OrderItemRepository;
import com.ecommerce.mulboutique.repository.OrderRepository;
import com.ecommerce.mulboutique.repository.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ShoppingCartRepository cartRepository;

    public AnalyticsResponse getStoreAnalytics(Long storeId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayStart = now.minusDays(1);
        LocalDateTime weekStart = now.minusDays(7);
        LocalDateTime monthStart = now.minusDays(30);
        LocalDateTime yearStart = now.minusDays(365);

        List<Order> ordersYear = orderRepository.findByStoreIdAndOrderDateBetween(storeId, yearStart, now);

        BigDecimal totalRevenue = ordersYear.stream()
                .map(Order::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> revenueByPeriod = new HashMap<>();
        revenueByPeriod.put("daily", sumRevenue(orderRepository.findByStoreIdAndOrderDateBetween(storeId, dayStart, now)));
        revenueByPeriod.put("weekly", sumRevenue(orderRepository.findByStoreIdAndOrderDateBetween(storeId, weekStart, now)));
        revenueByPeriod.put("monthly", sumRevenue(orderRepository.findByStoreIdAndOrderDateBetween(storeId, monthStart, now)));
        revenueByPeriod.put("yearly", totalRevenue);

        Map<String, Long> ordersByPeriod = new HashMap<>();
        ordersByPeriod.put("daily", (long) orderRepository.findByStoreIdAndOrderDateBetween(storeId, dayStart, now).size());
        ordersByPeriod.put("weekly", (long) orderRepository.findByStoreIdAndOrderDateBetween(storeId, weekStart, now).size());
        ordersByPeriod.put("monthly", (long) orderRepository.findByStoreIdAndOrderDateBetween(storeId, monthStart, now).size());
        ordersByPeriod.put("yearly", (long) ordersYear.size());

        List<Object[]> topProductsRaw = orderItemRepository.findTopProductsByStoreAndDateRange(storeId, monthStart, now);
        List<TopProductDto> topProducts = topProductsRaw.stream().map(row -> {
            TopProductDto dto = new TopProductDto();
            dto.setProductId((Long) row[0]);
            dto.setProductName((String) row[1]);
            dto.setTotalQuantity(((Number) row[2]).longValue());
            dto.setTotalRevenue(new BigDecimal(row[3].toString()));
            return dto;
        }).collect(Collectors.toList());

        long cartsCount = cartRepository.countByStoreIdAndCreatedAtBetween(storeId, monthStart, now);
        long ordersCount = ordersByPeriod.get("monthly");
        double conversionRate = cartsCount == 0 ? 0.0 : (double) ordersCount / (double) cartsCount;

        AnalyticsResponse response = new AnalyticsResponse();
        response.setStoreId(storeId);
        response.setTotalRevenue(totalRevenue);
        response.setRevenueByPeriod(revenueByPeriod);
        response.setOrdersByPeriod(ordersByPeriod);
        response.setTopProducts(topProducts);
        response.setConversionRate(conversionRate);

        return response;
    }

    private BigDecimal sumRevenue(List<Order> orders) {
        return orders.stream().map(Order::getFinalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
