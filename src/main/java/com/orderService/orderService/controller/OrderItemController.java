package com.orderService.orderService.controller;

import com.orderService.orderService.dto.OrderItem.OrderItemRequestDTO;
import com.orderService.orderService.dto.OrderItem.UpdateOrderItemRequestDTO;
import com.orderService.orderService.model.OrderItem;
import com.orderService.orderService.service.OrderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orderItem")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderItem> createOrderItem(@PathVariable Long orderId, @RequestBody OrderItem orderItem) {
        OrderItem savedOrderItem = orderItemService.saveOrderItem(orderId, orderItem);
        return ResponseEntity.ok(savedOrderItem);
    }

    @GetMapping("/findorderitembyid/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long id) {
        return orderItemService.getOrderItemById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOrderItem(@PathVariable Long id, @RequestBody UpdateOrderItemRequestDTO dto) {
        return orderItemService.updateOrderItem(id, dto);
    }

}
