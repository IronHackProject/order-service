package com.orderService.orderService.Test.OrderItemTests.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderService.orderService.controller.OrderItemController;
import com.orderService.orderService.dto.OrderItem.OrderItemRequestDTO;
import com.orderService.orderService.dto.OrderItem.UpdateOrderItemRequestDTO;
import com.orderService.orderService.exception.customException.OrderItemException;
import com.orderService.orderService.model.Order;

import com.orderService.orderService.model.OrderItem;
import com.orderService.orderService.service.OrderItemService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;


import org.springframework.http.MediaType;


@ExtendWith(SpringExtension.class)
@WebMvcTest(OrderItemController.class)
public class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderItemService orderItemService;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    void createOrderItem_shouldReturnCreated() throws Exception {
        OrderItemRequestDTO dto = new OrderItemRequestDTO();
        dto.setCustomerEmail("customer@example.com");
        dto.setProductId(100L);
        dto.setQuantity(3);
        dto.setOrderId(1L);

        Order otherOrder = new Order();
        otherOrder.setId(1L);
        otherOrder.setUserId(42L);
        otherOrder.setTotalAmount(59.99);
        otherOrder.setOrderDate(LocalDateTime.now());
        otherOrder.setOrderItems(new ArrayList<>());

        Mockito.when(orderItemService.saveOrderItem(eq(1L), any(OrderItemRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(otherOrder));

        mockMvc.perform(post("/api/order/item/1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(42L))
                .andExpect(jsonPath("$.totalAmount").value(59.99))
                .andExpect(jsonPath("$.orderDate").exists())
                .andExpect(jsonPath("$.orderItems").isEmpty());

    }

    @Test
    void createOrderItem_shouldReturnOk() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setUserId(10L);
        order.setTotalAmount(100.0);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(new ArrayList<>());

        OrderItemRequestDTO dto = new OrderItemRequestDTO("test@example.com", null, 2L, 2);

        Mockito.when(orderItemService.saveOrderItem(eq(1L), any(OrderItemRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(order));

        mockMvc.perform(post("/api/order/item/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
    @Test
    void createOrderItem_whenCustomerEmailIsNull_shouldReturnBadRequest() throws Exception {
        //JSON sin customerEmail (null)
        String jsonWithoutEmail = """
                {
                    "orderId": 1,
                    "productId": 2,
                    "quantity": 5
                }
                """;

        mockMvc.perform(post("/api/order/item/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithoutEmail))
                .andExpect(status().isBadRequest());

    }
    @Test
    void createOrderItem_userNotFound_shouldReturnNotFound() throws Exception {
        OrderItemRequestDTO dto = new OrderItemRequestDTO("unknown@example.com", null, 2L, 2);

        Mockito.when(orderItemService.saveOrderItem(eq(1L), any(OrderItemRequestDTO.class)))
                .thenThrow(new OrderItemException("User with email " + dto.getCustomerEmail() + " does not exist."));

        mockMvc.perform(post("/api/order/item/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User with email " + dto.getCustomerEmail() + " does not " +
                        "exist.")));
    }
    @Test
    void createOrderItem_whenCustomerEmailIsBlank_shouldReturnBadRequest() throws Exception {
        // DTO con email vacío
        OrderItemRequestDTO dto = new OrderItemRequestDTO("", 1L, 2L, 5);

        mockMvc.perform(post("/api/order/item/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

    }



    @Test
    void getOrderItemById_shouldReturnOrderItem() throws Exception {
        // se crea un  Order ya que un OrderIten siempre esta asociado a un Order
        // ticket con muchos items
        Order otherOrder = new Order();
        otherOrder.setId(1L);
        otherOrder.setUserId(42L);
        otherOrder.setTotalAmount(59.99);
        otherOrder.setOrderDate(LocalDateTime.now());
        otherOrder.setOrderItems(new ArrayList<>());


        // item de cada producto del ticket
        Long orderItemId = 1L;
        OrderItem item = new OrderItem();
        item.setId(orderItemId);
        item.setOrder(otherOrder);
        item.setUserId(42L);
        item.setProductId(1L);
        item.setQuantity(3);
        item.setTotalPrice(59.99);



        Mockito.when(orderItemService.getOrderItemById(orderItemId))
                .thenReturn(ResponseEntity.ok(item));

        mockMvc.perform(get("/api/order/item/findbyid/" + orderItemId))
                // expect Item
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderItemId))
                .andExpect(jsonPath("$.userId").value(42L))
                .andExpect(jsonPath("$.totalPrice").value(59.99))
                .andExpect(jsonPath("$.productId").value(1L))
                .andExpect(jsonPath("$.quantity").value(3));

    }
    @Test
    void updateOrderItem_shouldReturnUpdatedMessage() throws Exception {
        UpdateOrderItemRequestDTO dto = new UpdateOrderItemRequestDTO();
        dto.setQuantity(5);
        dto.setProductId(1L);

        Mockito.when(orderItemService.updateOrderItem(eq(1L), any(UpdateOrderItemRequestDTO.class)))
                .thenReturn(ResponseEntity.status(200).body("Order item updated successfully"));

        mockMvc.perform(put("/api/order/item/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Order item updated successfully"));
    }
    @Test
    void updateOrderItem_shouldReturnOk() throws Exception {
        UpdateOrderItemRequestDTO dto = new UpdateOrderItemRequestDTO(5L, 3);

        Mockito.when(orderItemService.updateOrderItem(eq(1L), any(UpdateOrderItemRequestDTO.class)))
                .thenReturn(ResponseEntity.ok("Order item updated successfully"));

        mockMvc.perform(put("/api/order/item/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Order item updated successfully"));
    }
    @Test
    void updateOrderItem_productNotFound_shouldReturnError() throws Exception {
        UpdateOrderItemRequestDTO dto = new UpdateOrderItemRequestDTO(999L, 1);

        Mockito.when(orderItemService.updateOrderItem(eq(1L), any(UpdateOrderItemRequestDTO.class)))
                .thenThrow(new OrderItemException("Product with id 999 does not exist."));

        mockMvc.perform(put("/api/order/item/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Product with id 999 does not exist.")));
    }


    @Test
    void deleteOrderItem_shouldReturnSuccessMessage() throws Exception {
        Mockito.when(orderItemService.deleteOrderItem(1L))
                .thenReturn(ResponseEntity.ok("Order item deleted successfully"));

        mockMvc.perform(delete("/api/order/item/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order item deleted successfully"));
    }

}
