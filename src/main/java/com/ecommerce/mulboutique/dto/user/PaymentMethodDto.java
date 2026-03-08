package com.ecommerce.mulboutique.dto.user;

import com.ecommerce.mulboutique.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(example = "{\"id\":1,\"methodType\":\"CARD\",\"brand\":\"VISA\",\"last4\":\"4242\",\"expMonth\":12,\"expYear\":2030,\"isDefault\":true}")
public class PaymentMethodDto {
    private Long id;
    private PaymentMethod.MethodType methodType;
    private String brand;
    private String last4;
    private Integer expMonth;
    private Integer expYear;
    private Boolean isDefault;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PaymentMethod.MethodType getMethodType() { return methodType; }
    public void setMethodType(PaymentMethod.MethodType methodType) { this.methodType = methodType; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getLast4() { return last4; }
    public void setLast4(String last4) { this.last4 = last4; }

    public Integer getExpMonth() { return expMonth; }
    public void setExpMonth(Integer expMonth) { this.expMonth = expMonth; }

    public Integer getExpYear() { return expYear; }
    public void setExpYear(Integer expYear) { this.expYear = expYear; }

    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
}
