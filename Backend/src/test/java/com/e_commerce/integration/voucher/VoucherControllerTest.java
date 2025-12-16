package com.e_commerce.integration.voucher;

import com.e_commerce.configuration.TestSecurityConfig;
import com.e_commerce.controller.voucher.VoucherController;
import com.e_commerce.dto.PageDTO;
import com.e_commerce.dto.voucher.VoucherCheck;
import com.e_commerce.dto.voucher.VoucherCreateForm;
import com.e_commerce.dto.voucher.VoucherDTO;
import com.e_commerce.dto.voucher.VoucherFilter;
import com.e_commerce.enums.VoucherType;
import com.e_commerce.integration.BaseWebMvcTest;
import com.e_commerce.service.voucher.VoucherService;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VoucherController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class VoucherControllerTest extends BaseWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    VoucherService voucherService;

    @Test
    @DisplayName("POST /vouchers/create creates voucher")
    void createVoucher_returnsCreated() throws Exception {
        VoucherDTO dto = VoucherDTO.builder()
                .id(1)
                .code("SALE10")
                .type(VoucherType.PERCENTAGE)
                .value(10.0)
                .isActive(true)
                .build();
        given(voucherService.createVoucher(any())).willReturn(dto);

        VoucherCreateForm form = VoucherCreateForm.builder()
                .code("SALE10")
                .type(VoucherType.PERCENTAGE)
                .value(10.0)
                .minOrderValue(new BigDecimal("50"))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .isActive(true)
                .build();

        mockMvc.perform(post("/vouchers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.code").value("SALE10"));
    }

    @Test
    @DisplayName("GET /vouchers returns page")
    void getVouchers_returnsPage() throws Exception {
        PageDTO<VoucherDTO> page = PageDTO.<VoucherDTO>builder()
                .content(List.of())
                .page(0)
                .size(10)
                .totalElements(0)
                .totalPages(0)
                .build();
        given(voucherService.getAllVouchers(anyInt(), anyInt(), any(VoucherFilter.class))).willReturn(page);

        mockMvc.perform(get("/vouchers")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(0));
    }

    @Test
    @DisplayName("DELETE /vouchers/delete/{id} removes voucher")
    void deleteVoucher_returnsOk() throws Exception {
        doNothing().when(voucherService).deleteVoucher(5);

        mockMvc.perform(delete("/vouchers/delete/{id}", 5))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /vouchers/check/{code} checks voucher")
    void checkVoucher_returnsResult() throws Exception {
        VoucherCheck check = VoucherCheck.builder()
                .valid(true)
                .message("Voucher is valid")
                .build();
        given(voucherService.checkVoucher("SALE10")).willReturn(check);

        mockMvc.perform(get("/vouchers/check/{code}", "SALE10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.message").value("Voucher is valid"));
    }
}
