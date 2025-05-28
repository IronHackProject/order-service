package com.orderService.orderService.Test.OrderTests.ControllerTest;

import com.orderService.orderService.controller.OrderController;

import com.orderService.orderService.model.Order;
import com.orderService.orderService.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(OrderController.class)
public class OrderControllerAdditionalTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;


    @Test
    void testInvalidEndpoint_returns404() throws Exception {
        mockMvc.perform(get("/api/orders/invalidEndpoint"))
                .andExpect(status().isNotFound());
    }


    @Test
    void testMethodNotAllowed_returns405() throws Exception {
        mockMvc.perform(post("/api/orders/findAll"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testFindAllOrders_returnsMultipleOrders() throws Exception {
        Order order1 = new Order(1L, 101L, LocalDateTime.now(), 200.0, List.of());
        Order order2 = new Order(2L, 102L, LocalDateTime.now(), 350.0, List.of());

        when(orderService.findAll()).thenReturn(List.of(order1, order2));

        mockMvc.perform(get("/api/orders/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].userId").value(101))
                .andExpect(jsonPath("$[1].userId").value(102));
    }

}
