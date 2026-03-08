package com.ecommerce.mulboutique.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(example = "{\"quantity\":3}")
public class UpdateCartItemRequest {
    @NotNull
    @Min(1)
    private Integer quantity;

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
