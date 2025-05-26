package com.orderService.orderService.controller;

import com.orderService.orderService.dto.OrderItemRequestDTO;
import com.orderService.orderService.model.OrderItem;
import com.orderService.orderService.service.OrderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orderItem")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public  OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping
    public ResponseEntity<?> createOrderItem(@RequestBody OrderItemRequestDTO dto) {
        return orderItemService.creteOrderItem(dto);
    }
}
