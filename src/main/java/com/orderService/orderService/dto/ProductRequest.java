package com.orderService.orderService.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotNull(message = "Product ID cannot be null")
    private long productId;
    @Positive(message = "Quantity must be a greater than zero")
    private int quantity;
}
