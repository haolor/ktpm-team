package com.e_commerce.integration.orders;

import com.e_commerce.configuration.TestSecurityConfig;
import com.e_commerce.controller.orders.OrderStatusHistoryController;
import com.e_commerce.dto.order.orderStatusHistoryDTO.OrderStatusHistoryDTO;
import com.e_commerce.integration.BaseWebMvcTest;
import com.e_commerce.service.order.OrderStatusHistoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderStatusHistoryController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class OrderStatusHistoryControllerTest extends BaseWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    OrderStatusHistoryService orderStatusHistoryService;

    @Test
    @DisplayName("GET /order-status-history/{orderId} returns history")
    void getHistory_returnsList() throws Exception {
        given(orderStatusHistoryService.getHistoryByOrderId(anyInt()))
                .willReturn(List.of(new OrderStatusHistoryDTO()));

        mockMvc.perform(get("/order-status-history/{orderId}", 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}
