package com.orderService.orderService.JunitTest;

import com.orderService.orderService.client.ProductClient;
import com.orderService.orderService.client.UserClient;
import com.orderService.orderService.dto.Order.OrderRequestDTO;
import com.orderService.orderService.dto.Product.ProductDTO;
import com.orderService.orderService.dto.Product.ProductRequest;
import com.orderService.orderService.model.Order;
import com.orderService.orderService.model.OrderItem;
import com.orderService.orderService.repository.OrderItemRespository;
import com.orderService.orderService.repository.OrderRepository;
import com.orderService.orderService.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.http.ResponseEntity;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRespository   orderItemRespository;
    @Mock
    private UserClient userClient;
    @Mock
    ProductClient productClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder_success() {
        OrderRequestDTO dto=new OrderRequestDTO();
        dto.setCustomerEmail("user@example.es");
        dto.setProducts(List.of(
                new ProductRequest(1L,2),
                new ProductRequest(2L,1)

        ));
        var userResponse=new com.orderService.orderService.dto.User.UserDTO();
        userResponse.setId(1L);
        when(userClient.findUserByEmail(dto.getCustomerEmail())).thenReturn(ResponseEntity.ok(userResponse));
        when(productClient.findById(1L)).thenReturn(ResponseEntity.ok(new ProductDTO(1L, "Prod 1", "desc", 10.0, 20,
                "ELECTRONICS")));
        when(productClient.findById(2L)).thenReturn(ResponseEntity.ok(new ProductDTO(2L, "Prod 2", "desc", 5.0, 10,
                "ELECTRONICS")));
        when(productClient.isProductAvailable(1L, 2)).thenReturn(true);
        when(productClient.isProductAvailable(2L, 1)).thenReturn(true);
        when(productClient.subQuantity(1L, 2)).thenReturn(ResponseEntity.ok().build());
        when(productClient.subQuantity(2L, 1)).thenReturn(ResponseEntity.ok().build());

        Order orderMock= new Order();
        orderMock.setId(100L);
        when(orderRepository.save(any(Order.class))).thenReturn(orderMock);

        when(orderItemRespository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });
        var response=orderService.createOrder(dto);
        assertEquals(200,response.getStatusCode().value());
        Order order=(Order) response.getBody();
        assertNotNull(order);
        assertEquals(2, order.getOrderItems().size());
        assertEquals(10.0*2+5.0*1,order.getTotalAmount(),0.01);

        verify(orderRepository,times(2)).save(any(Order.class));
        verify(orderItemRespository,times(2)).save(any(OrderItem.class));
        verify(userClient,times(1)).findUserByEmail(dto.getCustomerEmail());
        verify(productClient,times(2)).subQuantity(anyLong(),anyInt());






    }

}
