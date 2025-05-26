package com.orderService.orderService.repository;

import com.orderService.orderService.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRespository extends JpaRepository<OrderItem, Long> {
}
