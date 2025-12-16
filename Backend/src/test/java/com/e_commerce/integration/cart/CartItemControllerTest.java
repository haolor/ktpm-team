package com.e_commerce.integration.cart;

import com.e_commerce.config.TestConfig;
import com.e_commerce.controller.cart.CartItemController;
import com.e_commerce.dto.order.cartItemDTO.CartItemCreateForm;
import com.e_commerce.dto.order.cartItemDTO.CartItemDTO;
import com.e_commerce.dto.order.cartItemDTO.CartItemUpdateForm;
import com.e_commerce.service.account.AccountService;
import com.e_commerce.service.account.token.TokenBlacklistService;
import com.e_commerce.service.order.CartItemsService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CartItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestConfig.class)
class CartItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CartItemsService cartItemsService;

        @MockBean
        JwtUtil jwtUtil;

        @MockBean
        AccountService accountService;

        @MockBean
        TokenBlacklistService tokenBlacklistService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("POST /cart-items/addCart creates cart item")
    void addCart_returnsCreated() throws Exception {
        CartItemCreateForm form = CartItemCreateForm.builder()
                .productId(10)
                .quantity(2)
                .note("note")
                .optionValueId(List.of(1, 2))
                .build();

        CartItemDTO dto = CartItemDTO.builder()
                .id(123)
                .productId(10)
                .quantity(2)
                .price(new BigDecimal("9.99"))
                .selected(false)
                .build();

        given(cartItemsService.addToCart(any())).willReturn(dto);

        mockMvc.perform(post("/cart-items/addCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(123))
                .andExpect(jsonPath("$.data.productId").value(10));
    }

    @Test
    @DisplayName("PUT /cart-items/{id} updates quantity")
    void updateCartItem_returnsOk() throws Exception {
        CartItemUpdateForm update = new CartItemUpdateForm(5);

        CartItemDTO dto = CartItemDTO.builder()
                .id(1)
                .quantity(5)
                .build();

        given(cartItemsService.updateCartItems(eq(1), any())).willReturn(dto);

        mockMvc.perform(put("/cart-items/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.quantity").value(5));
    }

    @Test
    @DisplayName("GET /cart-items?selected=true returns selected items")
    void getSelected_returnsOk() throws Exception {
        given(cartItemsService.getCartItemsAllSelected()).willReturn(List.of());

        mockMvc.perform(get("/cart-items")
                        .param("selected", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("PUT /cart-items/{id}/quantity updates quantity")
    void updateQuantity_returnsOk() throws Exception {
        CartItemDTO dto = CartItemDTO.builder()
                .id(7)
                .quantity(3)
                .build();

        given(cartItemsService.updateCartItemQuantity(eq(7), eq(3))).willReturn(dto);

        mockMvc.perform(put("/cart-items/{id}/quantity", 7)
                        .param("quantity", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.quantity").value(3));
    }
}
