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
    private final OrderRepository orderRepository;

    public OrderItemService(ProductClient productClient, OrderItemRespository orderItemRespository,
                            UserClient userClient, OrderRepository orderRepository) {
        this.productClient = productClient;
        this.orderItemRespository = orderItemRespository;
        this.userClient = userClient;
        this.orderRepository = orderRepository;
    }

    public ResponseEntity<?> saveOrderItem(Long orderId, OrderItemRequestDTO orderItem) {

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



        // Buscar la orden asociada y sino la crea
        Order order = orderRepository.findById(orderId).orElseGet(() -> {
            Order newOrder = new Order();
            newOrder.setUserId(userResponse.getBody().getId());
            newOrder.setOrderDate(LocalDateTime.now());
            newOrder.setOrderItems(new ArrayList<>());
            newOrder.setTotalAmount(0.0);
            return orderRepository.save(newOrder);
        });


        // calcular la cantidad total
        double totalAmount = productResponse.getBody().getPrice() * orderItem.getQuantity();
        // guardar OrderItem
        OrderItem orderItemToSave = new OrderItem();
        orderItemToSave.setUserId(userResponse.getBody().getId());
        orderItemToSave.setProductId(orderItem.getProductId());
        orderItemToSave.setQuantity(orderItem.getQuantity());
        orderItemToSave.setTotalPrice(totalAmount);
        orderItemToSave.setOrder(order);

        // agregar el  OrderItem a la orden
        order.getOrderItems().add(orderItemToSave);
        // Recalcular el totalAmount
        double newTotal = order.getOrderItems().stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
        order.setTotalAmount(newTotal);

        orderItemRespository.save(orderItemToSave);

        return ResponseEntity.ok(order);
    }


    public ResponseEntity<OrderItem> getOrderItemById(Long id) {
        return orderItemRespository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new OrderItemException("Order item not found with id: " + id));
    }

    public ResponseEntity<?> updateOrderItem(Long id, UpdateOrderItemRequestDTO dto) {
        OrderItem orderItem = orderItemRespository.findById(id)
                .orElseThrow(() -> new OrderItemException("Order item not found with id: " + id));

        // Validar que el producto exista
        var productResponse = productClient.findById(dto.getProductId());
        if (!productResponse.getStatusCode().is2xxSuccessful() || productResponse.getBody() == null) {
            throw new OrderItemException("Product with id " + dto.getProductId() + " does not exist.");
        }

        // Recalcular total
        double newTotalPrice = productResponse.getBody().getPrice() * dto.getQuantity();

        orderItem.setProductId(dto.getProductId());
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setTotalPrice(newTotalPrice);

        // Guardar cambios
        orderItemRespository.save(orderItem);

        // Recalcular total de la orden
        Order order = orderItem.getOrder();
        double updatedOrderTotal = order.getOrderItems().stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
        order.setTotalAmount(updatedOrderTotal);
        orderRepository.save(order);

        return ResponseEntity.ok("Order item updated successfully");
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
