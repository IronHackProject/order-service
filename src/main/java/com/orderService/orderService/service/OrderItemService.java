package com.orderService.orderService.service;

import com.orderService.orderService.client.ProductClient;
import com.orderService.orderService.client.UserClient;
import com.orderService.orderService.dto.OrderItemRequestDTO;
import com.orderService.orderService.dto.ProductRequest;
import com.orderService.orderService.exception.customException.OrderItemException;
import com.orderService.orderService.model.OrderItem;
import com.orderService.orderService.repository.OrderItemRespository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {
    private final ProductClient productClient;
    private final OrderItemRespository orderItemRespository;
    private final UserClient userClient;

    public OrderItemService(ProductClient productClient, OrderItemRespository orderItemRespository,UserClient userClient) {
        this.productClient = productClient;
        this.orderItemRespository = orderItemRespository;
        this.userClient = userClient;
    }

    public ResponseEntity<?> creteOrderItem(OrderItemRequestDTO dto) {
        // validate if exist customer
        var userResponse = userClient.findUserByEmail(dto.getCustomerEmail());
        if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
            throw new OrderItemException("User with email " + dto.getCustomerEmail() + " does not exist.");
        }
        for (ProductRequest product : dto.getProducts()) {
            // check if exit productId
            ResponseEntity<?> productResponse = productClient.findById(product.getProductId());
            if (!productResponse.getStatusCode().is2xxSuccessful() || productResponse.getBody() == null) {
                throw new OrderItemException("Product with ID " + product.getProductId() +"does not exist.");
            }
            // check if product quantity is more than zero, vamos a comprobar que hay cantidad suficiente en el stock
            ResponseEntity<?> subQuantityResponse = productClient.subQuantity(product.getProductId(), product.getQuantity());
            if (!subQuantityResponse.getStatusCode().is2xxSuccessful() || subQuantityResponse.getBody() == null) {
                throw new OrderItemException("Not enough quantity for product with ID " + product.getProductId());
            }
            // create order item
            OrderItem orderItem = new OrderItem();
            System.out.println(userResponse);

        }







    }
}
