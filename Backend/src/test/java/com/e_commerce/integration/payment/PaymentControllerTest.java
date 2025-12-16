package com.e_commerce.integration.payment;

import com.e_commerce.config.TestConfig;
import com.e_commerce.controller.payment.PaymentController;
import com.e_commerce.dto.payment.PaymentDTO.PaymentDTO;
import com.e_commerce.service.account.AccountService;
import com.e_commerce.service.account.token.TokenBlacklistService;
import com.e_commerce.service.payment.PaymentService;
import com.e_commerce.util.JwtUtil;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestConfig.class)
class PaymentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PaymentService paymentService;

    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    AccountService accountService;

    @MockBean
    TokenBlacklistService tokenBlacklistService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("GET /payments/pay returns payment url")
    void createPayment_returnsOk() throws Exception {
        PaymentDTO dto = PaymentDTO.builder()
                .code("ok")
                .message("success")
                .paymentUrl("http://pay.test")
                .build();

        given(paymentService.createPayment(any())).willReturn(dto);

        mockMvc.perform(get("/payments/pay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("ok"));
    }

    @Test
    @DisplayName("POST /payments/vnpay/callback handles response")
    void paymentCallback_returnsOk() throws Exception {
        doNothing().when(paymentService).paymentCallback(any());

        mockMvc.perform(post("/payments/vnpay/callback"))
                .andExpect(status().isOk());
    }
}
