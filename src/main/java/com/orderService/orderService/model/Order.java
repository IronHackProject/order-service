package com.orderService.orderService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    LocalDateTime orderDate;
    private double totalAmount;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,orphanRemoval = true)
    List <OrderItem> orderItems;
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