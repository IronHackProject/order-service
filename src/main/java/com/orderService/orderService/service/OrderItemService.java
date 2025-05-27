package com.orderService.orderService.service;

import com.orderService.orderService.client.ProductClient;
import com.orderService.orderService.client.UserClient;
import com.orderService.orderService.dto.OrderItem.OrderItemRequestDTO;
import com.orderService.orderService.dto.OrderItem.UpdateOrderItemRequestDTO;
import com.orderService.orderService.dto.Product.ProductDTO;
import com.orderService.orderService.dto.Product.ProductRequest;

import com.orderService.orderService.exception.customException.OrderItemException;
import com.orderService.orderService.model.Order;
import com.orderService.orderService.model.OrderItem;
import com.orderService.orderService.repository.OrderItemRespository;
import com.orderService.orderService.repository.OrderRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {
    private final ProductClient productClient;
    private final OrderItemRespository orderItemRespository;
    private final UserClient userClient;
    private final OrderRepository  orderRepository;

    public OrderItemService(ProductClient productClient, OrderItemRespository orderItemRespository,
                            UserClient userClient,OrderRepository orderRepository) {
        this.productClient = productClient;
        this.orderItemRespository = orderItemRespository;
        this.userClient = userClient;
        this.orderRepository = orderRepository;
    }

    public ResponseEntity<?> saveOrderItem(Long orderId, OrderItemRequestDTO orderItem) {
        // Buscar la orden asociada
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderItemException("Order not found"));
        // validate if exist customer by email
        var userResponse = userClient.findUserByEmail(orderItem.getCustomerEmail());
        if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
            throw new OrderItemException("User with email " + orderItem.getCustomerEmail() + " does not exist.");
        }
        // validate if product exist
        ResponseEntity<ProductDTO> productResponse = productClient.findById(orderItem.getProductId());
        if (!productResponse.getStatusCode().is2xxSuccessful() || productResponse.getBody() == null) {
            throw new OrderItemException("Product with id " + orderItem.getProductId() + " does not exist.");
        }
        // calcular la cantidad total
        double totalAmount = productResponse.getBody().getPrice() * orderItem.getQuantity();
        // guardar OrderItem
        OrderItem orderItemToSave = new OrderItem();
        orderItemToSave.setUserId(userResponse.getBody().getId());
        orderItemToSave.setProductId(orderItem.getProductId());
        orderItemToSave.setQuantity(orderItem.getQuantity());
        orderItemToSave.setTotalPrice(totalAmount);
        orderItemToSave.setOrder(order);
        // guardar OrderItem
        orderItemRespository.save(orderItemToSave);

        return ResponseEntity.ok(order);
    }






    public ResponseEntity<OrderItem> getOrderItemById(Long id) {
        return orderItemRespository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new OrderItemException("Order item not found with id: " + id));
    }

    public ResponseEntity<?> updateOrderItem(Long id, UpdateOrderItemRequestDTO dto) {
        Optional<OrderItem> orderItem=orderItemRespository.findById(id);
        if (orderItem.isPresent()) {
            orderItem.get().setProductId(dto.getProductId());
            orderItem.get().setQuantity(dto.getQuantity());
            orderItemRespository.save(orderItem.get());
            return ResponseEntity.ok("Order item updated successfully");
        }

            throw new OrderItemException("Order item not found with id: " + id);

    }


    public ResponseEntity<String> deleteOrderItem(Long id) {
        Optional<OrderItem> orderItem = orderItemRespository.findById(id);
        if (orderItem.isPresent()) {
            orderItemRespository.delete(orderItem.get());
            return ResponseEntity.ok("Order item deleted successfully");
        } else {
            throw new OrderItemException("Order item not found with id: " + id);
        }
    }
}
