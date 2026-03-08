package com.ecommerce.mulboutique.dto.user;

import com.ecommerce.mulboutique.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(example = "{\"methodType\":\"CARD\",\"brand\":\"VISA\",\"last4\":\"4242\",\"expMonth\":12,\"expYear\":2030,\"isDefault\":true}")
public class PaymentMethodRequest {
    @NotNull
    private PaymentMethod.MethodType methodType;

    @NotBlank
    @Size(max = 20)
    private String brand;

    @NotBlank
    @Size(max = 4)
    private String last4;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer expMonth;

    @NotNull
    @Min(2024)
    @Max(2100)
    private Integer expYear;
    private Boolean isDefault = false;

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
