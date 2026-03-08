package com.ecommerce.mulboutique.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.ecommerce.mulboutique.entity.Order;

@Schema(example = "{\"storeId\":1,\"shippingAddress\":\"10 rue Exemple, Dakar, 11000, Senegal\",\"billingAddress\":\"10 rue Exemple, Dakar, 11000, Senegal\",\"paymentMethod\":\"CARD\",\"shippingMethod\":\"STANDARD\"}")
public class CreateOrderRequest {
    @NotNull
    private Long storeId;

    @NotBlank
    private String shippingAddress;
    private String billingAddress;
    private String couponCode;
    private String paymentMethod;
    private Order.ShippingMethod shippingMethod;

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Order.ShippingMethod getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(Order.ShippingMethod shippingMethod) { this.shippingMethod = shippingMethod; }
}
