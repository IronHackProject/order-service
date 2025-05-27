package com.orderService.orderService.controller;

import com.orderService.orderService.dto.Order.OrderRequestDTO;
import com.orderService.orderService.dto.Order.UpdateOrderRequestDTO;
import com.orderService.orderService.dto.OrderItem.OrderItemRequestDTO;
import com.orderService.orderService.exception.customException.OrderException;
import com.orderService.orderService.model.Order;
import com.orderService.orderService.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderRequestDTO dto) {
        return orderService.createOrder(dto);

    }



    @GetMapping("/findAll")
    public ResponseEntity<List<Order>> findAll() {
        List<Order>allOrders= orderService.findAll();
        if (allOrders.isEmpty()) {
            throw new RuntimeException("No orders found");
        }
        return ResponseEntity.ok(allOrders);
    }



    @PutMapping("/updateorder/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody UpdateOrderRequestDTO dto) {
        Order updatedOrder = orderService.updateOrder(id, dto);
        if (updatedOrder == null) {
            throw new RuntimeException("Order not found with id: " + id);
        }
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        boolean isDeleted = orderService.deleteOrder(id);
        if (!isDeleted) {
            throw new OrderException("Order not found with id: " + id);
        }
        return ResponseEntity.ok("Order deleted successfully");
    }












}
