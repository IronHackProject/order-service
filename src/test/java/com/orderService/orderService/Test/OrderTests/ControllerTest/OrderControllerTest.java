package com.orderService.orderService.Test.OrderTests.ControllerTest;

import com.orderService.orderService.controller.OrderController;
import com.orderService.orderService.dto.Order.UpdateOrderRequestDTO;
import com.orderService.orderService.exception.customException.OrderException;
import com.orderService.orderService.model.Order;
import com.orderService.orderService.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;



import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(OrderController.class)

public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private OrderService orderService;

    @Test
    void testFindAllOrders_throwsExceptionWhenEmpty() throws Exception {
        when(orderService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/orders/findAll"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof OrderException))
                .andExpect(result ->
                        assertEquals("No orders found", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void testFindAllOrders_returnsListOfOrders() throws Exception {
        Order order = new Order(1L, 100L, LocalDateTime.now(), 150.0, List.of());

        when(orderService.findAll()).thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].userId").value(100));
    }
    @Test
    void testDeleteOrder_success() throws Exception {
        Long orderId = 1L;
        when(orderService.deleteOrder(orderId)).thenReturn(true);

        mockMvc.perform(delete("/api/orders/delete/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().string("Order deleted successfully"));
    }


    @Test
    void testDeleteOrder_notFound() throws Exception {
        Long orderId = 999L;
        when(orderService.deleteOrder(orderId)).thenReturn(false);

        mockMvc.perform(delete("/api/orders/delete/{id}", orderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Order not found with id: " + orderId));
    }
    @Test
    void testUpdateOrder_success() throws Exception {
        Long orderId = 1L;
        UpdateOrderRequestDTO dto = new UpdateOrderRequestDTO();
        // configura dto con datos válidos

        Order updatedOrder = new Order(orderId, 100L, LocalDateTime.now(), 200.0, List.of());
        when(orderService.updateOrder(eq(orderId), any(UpdateOrderRequestDTO.class))).thenReturn(updatedOrder);

        mockMvc.perform(put("/api/orders/updateorder/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.totalAmount").value(200.0));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
