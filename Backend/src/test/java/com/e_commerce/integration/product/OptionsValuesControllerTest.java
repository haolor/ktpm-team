package com.e_commerce.integration.product;

import com.e_commerce.configuration.TestSecurityConfig;
import com.e_commerce.controller.product.OptionsValuesController;
import com.e_commerce.dto.product.optionValuesDTO.OptionValuesCreateDTO;
import com.e_commerce.dto.product.optionValuesDTO.OptionValuesDTO;
import com.e_commerce.dto.product.optionValuesDTO.OptionValuesUpdateDTO;import com.e_commerce.integration.BaseWebMvcTest;import com.e_commerce.service.product.OptionsValuesService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OptionsValuesController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class OptionsValuesControllerTest extends BaseWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OptionsValuesService optionsValuesService;

    @Test
    @DisplayName("GET /options-values/variant-option/{id} returns values")
    void getByOption_returnsList() throws Exception {
        given(optionsValuesService.getVariantValuesByVariantOptionId(anyInt()))
                .willReturn(List.of(OptionValuesDTO.builder().id(1).build()));

        mockMvc.perform(get("/options-values/variant-option/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("POST /options-values/create creates value")
    void createOptionsValue_returnsCreated() throws Exception {
        OptionValuesDTO dto = OptionValuesDTO.builder()
                .id(11)
                .value("Large")
                .price(new BigDecimal("1.5"))
                .build();
        given(optionsValuesService.createOptionValues(any())).willReturn(dto);

        OptionValuesCreateDTO create = OptionValuesCreateDTO.builder()
                .name("Large")
                .price(new BigDecimal("1.5"))
                .stockQuantity(10)
                .optionsGroupId(3)
                .build();

        mockMvc.perform(post("/options-values/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(11));
    }

    @Test
    @DisplayName("PUT /options-values/update/{id} updates value")
    void updateOptionsValue_returnsOk() throws Exception {
        OptionValuesDTO dto = OptionValuesDTO.builder()
                .id(11)
                .value("Updated")
                .build();
        given(optionsValuesService.updateVariantValue(any(), anyInt())).willReturn(dto);

        OptionValuesUpdateDTO update = OptionValuesUpdateDTO.builder()
                .value("Updated")
                .price(new BigDecimal("2.0"))
                .stockQuantity(5)
                .build();

        mockMvc.perform(put("/options-values/update/{id}", 11)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.value").value("Updated"));
    }
}
