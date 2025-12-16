package com.e_commerce.integration.product;

import com.e_commerce.configuration.TestSecurityConfig;
import com.e_commerce.controller.product.OptionsGroupController;
import com.e_commerce.dto.product.optionGroupDTO.OptionsGroupCreateDTO;
import com.e_commerce.dto.product.optionGroupDTO.OptionsGroupDTO;
import com.e_commerce.dto.product.optionGroupDTO.OptionsGroupUpdateDTO;
import com.e_commerce.enums.SelectionType;
import com.e_commerce.integration.BaseWebMvcTest;
import com.e_commerce.service.product.OptionsGroupService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OptionsGroupController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class OptionsGroupControllerTest extends BaseWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OptionsGroupService optionsGroupService;

    @Test
    @DisplayName("GET /options-group/category/{id} returns list")
    void getByCategory_returnsList() throws Exception {
        given(optionsGroupService.getOptionGroupsByProductId(anyInt()))
                .willReturn(List.of(OptionsGroupDTO.builder().id(1).build()));

        mockMvc.perform(get("/options-group/category/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("POST /options-group/create creates group")
    void createOptionsGroup_returnsCreated() throws Exception {
        OptionsGroupDTO dto = OptionsGroupDTO.builder()
                .id(10)
                .name("Size")
                .build();
        given(optionsGroupService.createOptionGroup(any())).willReturn(dto);

        OptionsGroupCreateDTO create = OptionsGroupCreateDTO.builder()
                .name("Size")
                .productId(2)
                .selectionType(SelectionType.SINGLE)
                .build();

        mockMvc.perform(post("/options-group/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(10));
    }

    @Test
    @DisplayName("PUT /options-group/update/{id} updates group")
    void updateOptionsGroup_returnsOk() throws Exception {
        OptionsGroupDTO dto = OptionsGroupDTO.builder()
                .id(10)
                .name("Updated")
                .build();
        given(optionsGroupService.updateVariantOption(any(), anyInt())).willReturn(dto);

        OptionsGroupUpdateDTO update = OptionsGroupUpdateDTO.builder()
                .name("Updated")
                .build();

        mockMvc.perform(put("/options-group/update/{id}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated"));
    }
}
