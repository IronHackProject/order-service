package com.orderService.orderService.dto.Order;

import com.orderService.orderService.model.OrderItem;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderRequestDTO {
    private Long userId;
    LocalDateTime orderDate;
    List<OrderItem> orderItems;
}
