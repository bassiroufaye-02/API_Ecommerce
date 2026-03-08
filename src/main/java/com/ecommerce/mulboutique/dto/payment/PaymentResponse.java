package com.ecommerce.mulboutique.dto.payment;

import com.ecommerce.mulboutique.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(example = "{\"orderId\":1,\"paymentStatus\":\"PAID\",\"paymentReference\":\"PAY-20260305-0001\"}")
public class PaymentResponse {
    private Long orderId;
    private Order.PaymentStatus paymentStatus;
    private String paymentReference;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Order.PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(Order.PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
}
