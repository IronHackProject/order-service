package com.orderService.orderService.Test.OrderTests.ServiceTest;

import com.orderService.orderService.client.ProductClient;
import com.orderService.orderService.client.UserClient;
import com.orderService.orderService.dto.Order.OrderRequestDTO;
import com.orderService.orderService.dto.Order.UpdateOrderRequestDTO;
import com.orderService.orderService.dto.Product.ProductDTO;
import com.orderService.orderService.dto.Product.ProductRequest;
import com.orderService.orderService.exception.customException.OrderException;
import com.orderService.orderService.model.Order;
import com.orderService.orderService.model.OrderItem;
import com.orderService.orderService.repository.OrderItemRespository;
import com.orderService.orderService.repository.OrderRepository;
import com.orderService.orderService.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
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
    private ProductClient productClient;



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
    @Test
    void testFindAll_returnsOrders() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<Order> result = orderService.findAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testUpdateOrder_success() {
        Long orderId = 1L;

        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setOrderItems(List.of(new OrderItem()));

        UpdateOrderRequestDTO dto = new UpdateOrderRequestDTO();
        dto.setUserId(123L);
        LocalDateTime now=LocalDateTime.now().withNano(0);
        dto.setOrderDate(now);
        OrderItem item= new OrderItem();
        item.setProductId(10L);
        item.setQuantity(2);
        dto.setOrderItems(List.of(item));


        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderItemRespository.save(any(OrderItem.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order updatedOrder = orderService.updateOrder(orderId, dto);

        assertEquals(dto.getUserId(), updatedOrder.getUserId());
        assertEquals(dto.getOrderItems().get(0).getProductId(), updatedOrder.getOrderItems().getFirst().getProductId());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderItemRespository, times(dto.getOrderItems().size())).save(any(OrderItem.class));
        verify(orderRepository, times(1)).save(existingOrder);
    }
    @Test
    void testUpdateOrder_notFound_throwsException() {
        Long orderId = 999L;
        UpdateOrderRequestDTO dto = new UpdateOrderRequestDTO();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        OrderException exception = assertThrows(OrderException.class, () -> {
            orderService.updateOrder(orderId, dto);
        });

        assertEquals("Order not found with id: " + orderId, exception.getMessage());
    }


    @Test
    void testDeleteOrder_success() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(order);

        boolean result = orderService.deleteOrder(orderId);

        assertTrue(result);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).delete(order);
    }
    @Test
    void testDeleteOrder_notFound() {
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        boolean result = orderService.deleteOrder(orderId);

        assertFalse(result);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).delete(any());
    }
    @Test
    void testIsValidLocalDateTime_validDate() {
        String validDate = "2025-05-28T15:30:00";
        assertTrue(orderService.isValidLocalDateTime(validDate));
    }

    @Test
    void testIsValidLocalDateTime_invalidDate() {
        String invalidDate = "28-05-2025 15:30";
        assertFalse(orderService.isValidLocalDateTime(invalidDate));
    }




}
