package com.orderService.orderService.MockMvc;

import com.orderService.orderService.controller.OrderController;
import com.orderService.orderService.exception.customException.OrderException;
import com.orderService.orderService.model.Order;
import com.orderService.orderService.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(OrderController.class)

public class OrderControllerFindAllTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
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


}
