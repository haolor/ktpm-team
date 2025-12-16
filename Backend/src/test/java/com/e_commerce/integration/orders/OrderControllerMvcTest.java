package com.e_commerce.integration.orders;

import com.e_commerce.config.TestConfig;
import com.e_commerce.controller.orders.OrderController;
import com.e_commerce.dto.PageDTO;
import com.e_commerce.dto.order.orderDTO.OrderCreateForm;
import com.e_commerce.dto.order.orderDTO.OrderDTO;
import com.e_commerce.enums.OrderStatus;
import com.e_commerce.service.account.AccountService;
import com.e_commerce.service.account.token.TokenBlacklistService;
import com.e_commerce.service.order.OrderService;
import com.e_commerce.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestConfig.class)
class OrderControllerMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OrderService orderService;

    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    AccountService accountService;

    @MockBean
    TokenBlacklistService tokenBlacklistService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("POST /orders/create returns created order")
    void createOrder_returnsCreated() throws Exception {
        OrderCreateForm form = OrderCreateForm.builder()
                .orderStatus(OrderStatus.PLACED)
                .listOrderItems(List.of(
                        com.e_commerce.dto.order.orderItemsDTO.OrderItemsCreateForm.builder()
                                .productId(1)
                                .quantity(1)
                                .build()
                ))
                .note("note")
                .userInfoId(1)
                .build();

        OrderDTO dto = new OrderDTO();
        dto.setId(999);
        dto.setOrderStatus(OrderStatus.PLACED.name());
        dto.setTotalPrice(new BigDecimal("10.00"));

        given(orderService.createOrder(any())).willReturn(dto);

        mockMvc.perform(post("/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(999))
                .andExpect(jsonPath("$.data.orderStatus").value("PLACED"));
    }

    @Test
    @DisplayName("PATCH /orders/update-status/{id} returns updated status")
    void updateStatus_returnsUpdated() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setId(1001);
        dto.setOrderStatus(OrderStatus.CONFIRMED.name());
        dto.setTotalPrice(new BigDecimal("20.00"));

        given(orderService.updateOrderStatus(eq(1001), eq(OrderStatus.CONFIRMED))).willReturn(dto);

        mockMvc.perform(patch("/orders/update-status/{id}", 1001)
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderStatus").value("CONFIRMED"));
    }

    @Test
    @DisplayName("GET /orders/restaurant/{id} returns page DTO")
    void getOrdersByRestaurant_returnsPage() throws Exception {
        PageDTO<OrderDTO> page = PageDTO.<OrderDTO>builder()
                .content(List.of())
                .page(0)
                .size(5)
                .totalElements(0)
                .totalPages(0)
                .build();

        given(orderService.getOrdersByRestaurant(anyInt(), anyInt(), eq(10))).willReturn(page);

        mockMvc.perform(get("/orders/restaurant/{id}", 10)
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.content").isArray());
    }
}
