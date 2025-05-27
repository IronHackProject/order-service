package com.orderService.orderService.service;

import com.orderService.orderService.client.ProductClient;
import com.orderService.orderService.client.UserClient;
import com.orderService.orderService.dto.OrderItemRequestDTO;
import com.orderService.orderService.dto.Product.ProductDTO;
import com.orderService.orderService.dto.ProductRequest;

import com.orderService.orderService.exception.customException.OrderItemException;
import com.orderService.orderService.model.Order;
import com.orderService.orderService.model.OrderItem;
import com.orderService.orderService.repository.OrderItemRespository;
import com.orderService.orderService.repository.OrderRepository;
import jakarta.persistence.JoinColumn;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public ResponseEntity<?> creteOrderItem(OrderItemRequestDTO dto) {
        // validate if exist customer
        var userResponse = userClient.findUserByEmail(dto.getCustomerEmail());
        if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
            throw new OrderItemException("User with email " + dto.getCustomerEmail() + " does not exist.");
        }
        List<OrderItem> orderItems = new ArrayList<>();
        for (ProductRequest product : dto.getProducts()) {
            // check if exit productId
            ResponseEntity<ProductDTO> productResponse = productClient.findById(product.getProductId());
            if (!productResponse.getStatusCode().is2xxSuccessful() || productResponse.getBody() == null) {
                throw new OrderItemException("Product with ID " + product.getProductId() +"does not exist.");
            }
            // check stock
            Boolean isAvailable = productClient.isProductAvailable(product.getProductId(), product.getQuantity());
            if (!isAvailable) {
                throw new OrderItemException("Product with ID " + product.getProductId() + " is not available in the requested quantity.");
            }

            // sub quantity of product
            ResponseEntity<?> subQuantityResponse = productClient.subQuantity(product.getProductId(), product.getQuantity());
            if (!subQuantityResponse.getStatusCode().is2xxSuccessful() || subQuantityResponse.getBody() == null) {
                throw new OrderItemException("Not enough quantity for product with ID " + product.getProductId());
            }

            // create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getProductId());
            orderItem.setUserId(userResponse.getBody().getId());
            orderItem.setQuantity(product.getQuantity());
            orderItem.setPrice(productResponse.getBody().getPrice() * product.getQuantity());
            orderItems.add(orderItem);
            // save orderItems
            orderItemRespository.save(orderItem);
        }

        // create order
        Order order = new Order();
        Long userId=userResponse.getBody().getId();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(orderItems);
        // calculate total price
        double totalPrice = calculateTotalPrice(orderItems);
        order.setTotalAmount(totalPrice);
        // save order item
        Order savedOrder = orderRepository.save(order);
        return ResponseEntity.ok().body(savedOrder);
    }


    public double calculateTotalPrice(List<OrderItem> orderItems) {
        double totalPrice = 0.0;
        for (OrderItem item : orderItems) {
            totalPrice+=item.getPrice()* item.getQuantity();
        }
        return totalPrice;
    }



}
