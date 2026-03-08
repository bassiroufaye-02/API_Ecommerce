package com.ecommerce.mulboutique.dto.order;

import com.ecommerce.mulboutique.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(example = "{\"status\":\"SHIPPED\"}")
public class UpdateOrderStatusRequest {
    @NotNull
    private Order.OrderStatus status;

    public Order.OrderStatus getStatus() { return status; }
    public void setStatus(Order.OrderStatus status) { this.status = status; }
}
