package com.ecommerce.mulboutique.dto.order;

import com.ecommerce.mulboutique.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(example = "{\"shippingStatus\":\"IN_TRANSIT\",\"shippingProvider\":\"DHL\",\"trackingNumber\":\"SN123456789\"}")
public class UpdateShippingRequest {
    private Order.ShippingStatus shippingStatus;
    private String shippingProvider;
    private String trackingNumber;

    public Order.ShippingStatus getShippingStatus() { return shippingStatus; }
    public void setShippingStatus(Order.ShippingStatus shippingStatus) { this.shippingStatus = shippingStatus; }

    public String getShippingProvider() { return shippingProvider; }
    public void setShippingProvider(String shippingProvider) { this.shippingProvider = shippingProvider; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
}
