package com.ecommerce.mulboutique.dto.coupon;

import com.ecommerce.mulboutique.entity.Coupon;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(example = "{\"id\":1,\"code\":\"PROMO10\",\"description\":\"Remise 10%\",\"discountType\":\"PERCENTAGE\",\"discountValue\":10,\"minimumAmount\":50,\"maximumDiscount\":30,\"usageLimit\":100,\"usageCount\":0,\"isActive\":true,\"validFrom\":\"2026-03-01T00:00:00\",\"validUntil\":\"2026-03-31T23:59:59\"}")
public class CouponDto {
    private Long id;
    private String code;
    private String description;
    private Coupon.DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minimumAmount;
    private BigDecimal maximumDiscount;
    private Integer usageLimit;
    private Integer usageCount;
    private Boolean isActive;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

    public LocalDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }
}
