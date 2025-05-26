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
