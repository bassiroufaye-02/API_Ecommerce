package com.ecommerce.mulboutique.dto.coupon;

import com.ecommerce.mulboutique.entity.Coupon;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(example = "{\"code\":\"PROMO10\",\"description\":\"Remise 10%\",\"discountType\":\"PERCENTAGE\",\"discountValue\":10,\"minimumAmount\":50,\"maximumDiscount\":30,\"usageLimit\":100,\"validFrom\":\"2026-03-01T00:00:00\",\"validUntil\":\"2026-03-31T23:59:59\"}")
public class CreateCouponRequest {
    @NotBlank
    @Size(max = 20)
    private String code;
    private String description;

    @NotNull
    private Coupon.DiscountType discountType;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal discountValue;
    private BigDecimal minimumAmount;
    private BigDecimal maximumDiscount;
    private Integer usageLimit;

    @NotNull
    private LocalDateTime validFrom;

    @NotNull
    private LocalDateTime validUntil;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Coupon.DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(Coupon.DiscountType discountType) { this.discountType = discountType; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }

    public BigDecimal getMinimumAmount() { return minimumAmount; }
    public void setMinimumAmount(BigDecimal minimumAmount) { this.minimumAmount = minimumAmount; }

    public BigDecimal getMaximumDiscount() { return maximumDiscount; }
    public void setMaximumDiscount(BigDecimal maximumDiscount) { this.maximumDiscount = maximumDiscount; }

    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }

    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

    public LocalDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }
}
