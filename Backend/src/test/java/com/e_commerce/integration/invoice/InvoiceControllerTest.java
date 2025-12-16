package com.e_commerce.integration.invoice;

import com.e_commerce.config.TestConfig;
import com.e_commerce.controller.invoice.InvoiceController;
import com.e_commerce.dto.PageDTO;
import com.e_commerce.dto.invoice.invoiceDTO.InvoiceCreateForm;
import com.e_commerce.dto.invoice.invoiceDTO.InvoiceDTO;
import com.e_commerce.dto.invoice.invoiceDetailsDTO.InvoiceDetailsCreateForm;
import com.e_commerce.service.account.AccountService;
import com.e_commerce.service.account.token.TokenBlacklistService;
import com.e_commerce.service.invoice.InvoiceService;
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
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InvoiceController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestConfig.class)
class InvoiceControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    InvoiceService invoiceService;

        @MockBean
        JwtUtil jwtUtil;

        @MockBean
        AccountService accountService;

        @MockBean
        TokenBlacklistService tokenBlacklistService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("POST /invoices/create creates invoice")
    void createInvoice_returnsCreated() throws Exception {
        InvoiceCreateForm form = InvoiceCreateForm.builder()
                .orderId(1)
                .staffId(2)
                .paymentMethodId(3)
                .shippingFee(BigDecimal.ZERO)
                .items(List.of(InvoiceDetailsCreateForm.builder()
                        .productId(10)
                        .optionValueId(20)
                        .quantity(1)
                        .unitPrice(new BigDecimal("99.99"))
                        .build()))
                .build();

        InvoiceDTO dto = InvoiceDTO.builder()
                .id(100)
                .invoiceCode("INV-1")
                .totalAmount(new BigDecimal("99.99"))
                .build();

        given(invoiceService.createInvoice(any())).willReturn(dto);

        mockMvc.perform(post("/invoices/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(100));
    }

    @Test
    @DisplayName("GET /invoices returns page")
    void getInvoices_returnsOk() throws Exception {
        PageDTO<InvoiceDTO> page = PageDTO.<InvoiceDTO>builder()
                .content(List.of())
                .page(0)
                .size(10)
                .totalElements(0)
                .totalPages(0)
                .build();

        given(invoiceService.getAllInvoices(anyInt(), anyInt(), any())).willReturn(page);

        mockMvc.perform(get("/invoices")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(0));
    }
}
