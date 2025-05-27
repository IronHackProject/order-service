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
    private Long orderId;
    private Long productId;
    private int quantity;
}
