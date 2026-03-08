package com.ecommerce.mulboutique.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(example = "{\"orderId\":1,\"paymentMethod\":\"CARD\"}")
public class PaymentRequest {
    @NotNull
    private Long orderId;
    private String paymentMethod;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
