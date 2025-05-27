package com.orderService.orderService.dto.OrderItem;

import com.orderService.orderService.dto.Product.ProductRequest;
import com.orderService.orderService.model.Order;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Orde")
    private Order order;
    private long userId;
    private Long productId;
    private int quantity;
    private double price;


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