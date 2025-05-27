package com.orderService.orderService.dto.OrderItem;

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
public class OrderItemRequestDTO {
    @NotBlank(message = "Customer email cannot be blank")
    private String customerEmail;
    @NotEmpty(message = "Product list cannot be empty")
    List<ProductRequest> products;
}
// Example JSON request body for creating an order item
//{
//        "customerEmail": "email@email.com",
//        "products": [
//        { "productId": 1, "quantity": 2 },
//        { "productId": 5, "quantity": 1 },
//        { "productId": 8, "quantity": 3 }
//        ]
//        }