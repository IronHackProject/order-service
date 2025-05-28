package com.orderService.orderService.Test.OrderItemTests.ServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

import com.orderService.orderService.client.ProductClient;
import com.orderService.orderService.client.UserClient;
import com.orderService.orderService.dto.OrderItem.OrderItemRequestDTO;
import com.orderService.orderService.dto.OrderItem.UpdateOrderItemRequestDTO;
import com.orderService.orderService.dto.Product.ProductDTO;
import com.orderService.orderService.dto.User.UserDTO;

import com.orderService.orderService.model.Order;
import com.orderService.orderService.model.OrderItem;
import com.orderService.orderService.repository.OrderItemRespository;
import com.orderService.orderService.repository.OrderRepository;

import com.orderService.orderService.service.OrderItemService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

public class OrderItemServiceTest {

    @Mock
    private ProductClient productClient;
    @Mock
    private OrderItemRespository orderItemRespository;
    @Mock
    private UserClient userClient;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderItemService orderItemService;


    private AutoCloseable closeable;
    @BeforeEach
    void setUp() {
        closeable=MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void saveOrderItem_shouldSaveAndReturnOrder() {
        // OrderItemRequestDTO

        OrderItemRequestDTO dto = new OrderItemRequestDTO();
        dto.setOrderId(1L);
        dto.setCustomerEmail("user@test.com");
        dto.setProductId(1L);
        dto.setQuantity(2);

        //ProductDTO
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Product");
        productDTO.setDescription("desc");
        productDTO.setPrice(10.0);
        productDTO.setQuantity(3);
        productDTO.setTypeProduct("ELECTRONICS");
        // UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setId(42L);
        userDTO.setName("Abelardo");
        userDTO.setSurname("Lopez");
        userDTO.setEmail("user@test.com");

        // Orde
        Order order = new Order();
        order.setId(1L);
        order.setOrderItems(new ArrayList<>());
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(0.0);
        order.setUserId(42L);

        // Mock de User
        var userMock = ResponseEntity.ok(userDTO);
        Mockito.when(userClient.findUserByEmail(userDTO.getEmail()))
                .thenReturn(userMock);

        // Mock de Producto
        ResponseEntity<ProductDTO> productMock = ResponseEntity.ok(productDTO);
        Mockito.when(productClient.findById(dto.getProductId()))
                .thenReturn(productMock);

        // mock de Order busca el pedido por ID
        Mockito.when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.of(order));
        // Mock de OrderItemRepository guarda el item del pedido
        Mockito.when(orderItemRespository.save(Mockito.any(OrderItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // Mock de OrderRepository guarda el pedido
        Mockito.when(orderRepository.save(Mockito.any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Order> response = orderItemService.saveOrderItem(dto.getOrderId(), dto);






        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(42L, response.getBody().getUserId());
        assertEquals(20.0, response.getBody().getTotalAmount());
        assertEquals(1, response.getBody().getOrderItems().size());

    }

    @Test
    void getOrderItemById_shouldReturnOrderItem() {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        when(orderItemRespository.findById(1L))
                .thenReturn(Optional.of(orderItem));

        ResponseEntity<OrderItem> response = orderItemService.getOrderItemById(1L);

        assertEquals(OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void updateOrderItem_shouldUpdateSuccessfully() {
        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        order.setOrderItems(List.of(orderItem));

        ProductDTO product = new ProductDTO(2L, "Caramelo", "Caramelo de azucar", 15.0, 1, "TOYS");
        UpdateOrderItemRequestDTO dto = new UpdateOrderItemRequestDTO(2L, 3);

        when(orderItemRespository.findById(1L)).thenReturn(Optional.of(orderItem));
        when(productClient.findById(2L)).thenReturn(ResponseEntity.ok(product));
        when(orderItemRespository.save(any(OrderItem.class))).thenReturn(orderItem);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        ResponseEntity<String> response = orderItemService.updateOrderItem(1L, dto);

        assertEquals(OK, response.getStatusCode());
        assertEquals("Order item updated successfully", response.getBody());
        assertEquals(45.0, orderItem.getTotalPrice());
    }

    @Test
    void deleteOrderItem_shouldDeleteSuccessfully() {
        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setTotalPrice(10.0);
        orderItem.setOrder(order);

        order.setOrderItems(List.of(orderItem));
        order.setTotalAmount(10.0);

        when(orderItemRespository.findById(1L)).thenReturn(Optional.of(orderItem));

        ResponseEntity<String> response = orderItemService.deleteOrderItem(1L);

        verify(orderItemRespository).delete(orderItem);
        verify(orderRepository).save(order);

        assertEquals(OK, response.getStatusCode());
        assertEquals("Order item deleted successfully", response.getBody());
    }
}