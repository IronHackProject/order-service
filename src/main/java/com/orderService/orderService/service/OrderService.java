package com.orderService.orderService.service;


import com.orderService.orderService.client.ProductClient;
import com.orderService.orderService.client.UserClient;
import com.orderService.orderService.dto.Order.OrderRequestDTO;
import com.orderService.orderService.dto.Order.UpdateOrderRequestDTO;

import com.orderService.orderService.dto.Product.ProductDTO;
import com.orderService.orderService.dto.Product.ProductRequest;
import com.orderService.orderService.exception.customException.OrderException;
import com.orderService.orderService.exception.customException.OrderItemException;
import com.orderService.orderService.model.Order;
import com.orderService.orderService.model.OrderItem;
import com.orderService.orderService.repository.OrderItemRespository;
import com.orderService.orderService.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRespository orderItemRespository;
    private final UserClient userClient;
    private final ProductClient productClient;




    public OrderService(OrderRepository orderRepository, OrderItemRespository orderItemRespository,
                        UserClient userClient,ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.orderItemRespository = orderItemRespository;
        this.userClient = userClient;
        this.productClient = productClient;
    }
    @Transactional
    public ResponseEntity<?> createOrder(OrderRequestDTO dto) {
        // Validar usuario
        var userResponse = userClient.findUserByEmail(dto.getCustomerEmail());
        if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
            throw new OrderItemException("User with email " + dto.getCustomerEmail() + " does not exist.");
        }

        Long userId = userResponse.getBody().getId();
        double totalAmount = 0.0;

        // Primero, validar que todos los productos existen y están disponibles
        for (ProductRequest product : dto.getProducts()) {
            var productResponse = productClient.findById(product.getProductId());
            if (!productResponse.getStatusCode().is2xxSuccessful() || productResponse.getBody() == null) {
                throw new OrderItemException("Product with ID " + product.getProductId() + " does not exist.");
            }

            Boolean isAvailable = productClient.isProductAvailable(product.getProductId(), product.getQuantity());
            if (!isAvailable) {
                throw new OrderItemException("Product with ID " + product.getProductId() + " is not available.");
            }
        }

        // Crear orden vacía
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(0.0); // provisional
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();

        for (ProductRequest product : dto.getProducts()) {
            // Descontar cantidad solo si ya se validó todo
            ResponseEntity<?> subQuantityResponse = productClient.subQuantity(product.getProductId(), product.getQuantity());
            if (!subQuantityResponse.getStatusCode().is2xxSuccessful()) {
                throw new OrderItemException("Not enough quantity for product with ID " + product.getProductId());
            }

            var productResponse = productClient.findById(product.getProductId());
            double price = productResponse.getBody().getPrice() * product.getQuantity();

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getProductId());
            orderItem.setUserId(userId);
            orderItem.setQuantity(product.getQuantity());
            orderItem.setTotalPrice(price);
            orderItem.setOrder(savedOrder);

            orderItemRespository.save(orderItem);
            orderItems.add(orderItem);
            totalAmount += price;
        }

        savedOrder.setOrderItems(orderItems);
        savedOrder.setTotalAmount(totalAmount);
        orderRepository.save(savedOrder); // solo una vez, si realmente necesitas actualizar el total

        return ResponseEntity.ok().body(savedOrder);
    }





    public List<Order> findAll() {
     return orderRepository.findAll();
    }


    public Order updateOrder(Long id, UpdateOrderRequestDTO dto) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            // order existente
            Order existingOrder = order.get();
            // setea el userId
            existingOrder.setUserId(dto.getUserId());
            // validate if orderDate is correct format
            boolean formatLocalDateTime = isValidLocalDateTime(dto.getOrderDate().toString());
            if (!formatLocalDateTime) {
                throw new OrderException("Invalid date format. Expected format: yyyy-MM-dd'T'HH:mm:ss");
            }
            // setea la fecha de la orden
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime dateTimeParsed=LocalDateTime.parse(dto.getOrderDate().toString(), formatter);
            existingOrder.setOrderDate(dateTimeParsed);

            // Lista de OrderItems de la orden existente
            List<OrderItem> orderList = existingOrder.getOrderItems();
            // recorrer la lista de OrderItems y actualizar cada uno
            for (int i = 0; i < orderList.size(); i++) {
                // orderItem existente
                OrderItem orderItem = orderList.get(i);
                // setea el id del Producto
                orderItem.setProductId(dto.getOrderItems().get(i).getProductId());
                // setea la cantidad del Producto
                orderItem.setQuantity(dto.getOrderItems().get(i).getQuantity());
                // guarda el OrderItem actualizado
                orderItemRespository.save(orderItem);
            }
            return orderRepository.save(existingOrder);
        }
        // si no existe la orden, lanza una excepción
        throw new OrderException("Order not found with id: " + id);
    }



    public boolean isValidLocalDateTime(String dateString) {
        try {
            LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public boolean deleteOrder(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            // si existe la orden, la elimina
            orderRepository.delete(order.get());
            return true;
        } else {
            // si no existe la orden, retorn false
           return false;
        }
    }


}
