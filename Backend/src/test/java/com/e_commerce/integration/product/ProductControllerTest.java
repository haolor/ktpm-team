package com.e_commerce.integration.product;

import com.e_commerce.config.TestConfig;
import com.e_commerce.controller.product.ProductController;
import com.e_commerce.dto.PageDTO;
import com.e_commerce.dto.product.productDTO.ProductDTO;
import com.e_commerce.dto.product.productDTO.ProductDetailDTO;
import com.e_commerce.dto.product.productDTO.ProductUpdateDTO;
import com.e_commerce.enums.AvailabilityStatus;
import com.e_commerce.service.account.AccountService;
import com.e_commerce.service.account.token.TokenBlacklistService;
import com.e_commerce.service.product.ProductService;
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
import org.springframework.mock.web.MockMultipartFile;
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

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestConfig.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ProductService productService;

        @MockBean
        JwtUtil jwtUtil;

        @MockBean
        AccountService accountService;

        @MockBean
        TokenBlacklistService tokenBlacklistService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("POST /products/create creates product")
    void createProduct_returnsCreated() throws Exception {
        MockMultipartFile image = new MockMultipartFile("imgMain", "img.jpg", "image/jpeg", "bytes".getBytes());

        ProductDTO dto = ProductDTO.builder()
                .id(11)
                .name("Pizza")
                .priceBase(new BigDecimal("12.50"))
                .status(AvailabilityStatus.ACTIVE)
                .build();

        given(productService.createProduct(any())).willReturn(dto);

        mockMvc.perform(multipart("/products/create")
                        .file(image)
                        .param("name", "Pizza")
                        .param("categoryId", "1")
                        .param("description", "Tasty")
                        .param("priceBase", "12.50")
                        .param("restaurantId", "5"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(11))
                .andExpect(jsonPath("$.data.name").value("Pizza"));
    }

    @Test
    @DisplayName("PUT /products/update/{id} updates product")
    void updateProduct_returnsOk() throws Exception {
        ProductUpdateDTO updateDTO = new ProductUpdateDTO();
        updateDTO.setName("Updated");
        updateDTO.setStatus(AvailabilityStatus.HIDDEN);

        ProductDTO dto = ProductDTO.builder()
                .id(11)
                .name("Updated")
                .status(AvailabilityStatus.HIDDEN)
                .build();

        given(productService.updateProduct(eq(11), any())).willReturn(dto);

        mockMvc.perform(put("/products/update/{id}", 11)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.status").value("HIDDEN"));
    }

    @Test
    @DisplayName("GET /products/detail/{id} returns detail")
    void getProductDetail_returnsOk() throws Exception {
        ProductDetailDTO detail = ProductDetailDTO.builder()
                .id(22)
                .name("Burger")
                .basePrice(new BigDecimal("8.99"))
                .status(AvailabilityStatus.ACTIVE)
                .build();

        given(productService.getProductDetail(eq(22))).willReturn(detail);

        mockMvc.perform(get("/products/detail/{id}", 22))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Burger"));
    }

    @Test
    @DisplayName("GET /products returns paged list")
    void getProducts_returnsOk() throws Exception {
        PageDTO<ProductDTO> page = PageDTO.<ProductDTO>builder()
                .content(List.of())
                .page(0)
                .size(12)
                .totalElements(0)
                .totalPages(0)
                .build();

        given(productService.getAllProductsAdmin(anyInt(), anyInt(), any())).willReturn(page);

        mockMvc.perform(get("/products")
                        .param("page", "1")
                        .param("size", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(0));
    }
}
