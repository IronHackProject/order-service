package com.orderService.orderService.dto.Order;

import com.orderService.orderService.dto.Product.ProductRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    @NotBlank(message = "Customer email cannot be blank")
    private String customerEmail;

    @NotEmpty(message = "Product list cannot be empty")
    private List<ProductRequest> products;
}
