package com.e_commerce.unit.cart;

import com.e_commerce.dto.order.cartItemDTO.CartItemCreateForm;
import com.e_commerce.dto.order.cartItemDTO.CartItemDTO;
import com.e_commerce.dto.order.cartItemDTO.CartItemUpdateForm;
import com.e_commerce.entity.order.CartItems;
import com.e_commerce.entity.order.Carts;
import com.e_commerce.entity.product.OptionValues;
import com.e_commerce.entity.product.Product;
import com.e_commerce.exceptions.CustomException;
import com.e_commerce.mapper.order.CartItemMapper;
import com.e_commerce.repository.order.CartItemsRepository;
import com.e_commerce.service.account.AccountService;
import com.e_commerce.service.order.CartsService;
import com.e_commerce.service.order.impl.CartItemsServiceImpl;
import com.e_commerce.service.product.OptionsValuesService;
import com.e_commerce.service.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CartItemsServiceImplTest {

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private CartItemsRepository cartItemsRepository;

    @Mock
    private CartsService cartsService;

    @Mock
    private ProductService productService;

    @Mock
    private OptionsValuesService optionsValuesService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private CartItemsServiceImpl cartItemsService;

    private Product testProduct;
    private Carts testCart;
    private CartItems testCartItem;
    private CartItemDTO testCartItemDTO;
    private CartItemCreateForm createForm;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Test Product");
        testProduct.setPriceBase(BigDecimal.valueOf(100));
        testProduct.setQuantity(50);

        testCart = new Carts();
        testCart.setId(1);

        testCartItem = new CartItems();
        testCartItem.setId(1);
        testCartItem.setProduct(testProduct);
        testCartItem.setCart(testCart);
        testCartItem.setQuantity(2);
        testCartItem.setPrice(BigDecimal.valueOf(100));

        testCartItemDTO = new CartItemDTO();
        testCartItemDTO.setId(1);
        testCartItemDTO.setProductId(1);
        testCartItemDTO.setQuantity(2);
        testCartItemDTO.setPrice(BigDecimal.valueOf(100));

        createForm = new CartItemCreateForm();
        createForm.setProductId(1);
        createForm.setQuantity(2);
        createForm.setNote("Test note");
    }

    @Test
    void getCartItemsById_Success() {
        // Arrange
        when(cartItemsRepository.findById(anyInt())).thenReturn(Optional.of(testCartItem));

        // Act
        CartItems result = cartItemsService.getCartItemsById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(cartItemsRepository).findById(1);
    }

    @Test
    void getCartItemsById_NotFound_ThrowsException() {
        // Arrange
        when(cartItemsRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomException.class, () -> cartItemsService.getCartItemsById(999));
    }

    @Test
    void addToCart_WithoutOptions_Success() {
        // Arrange
        when(productService.getProductEntityById(1)).thenReturn(testProduct);
        when(cartsService.createCarts()).thenReturn(testCart);
        when(cartItemsRepository.findByCartIdAndProductIdAndOptionValues(eq(1), eq(1), isNull(), eq(0L)))
                .thenReturn(Optional.empty());
        when(cartItemMapper.convertCreateDTOToEntity(any(CartItemCreateForm.class))).thenReturn(new CartItems());
        when(cartItemsRepository.save(any(CartItems.class))).thenReturn(testCartItem);
        when(cartItemMapper.convertEntityToDTO(any(CartItems.class))).thenReturn(testCartItemDTO);

        // Act
        CartItemDTO result = cartItemsService.addToCart(createForm);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getProductId());
        verify(cartItemsRepository).save(any(CartItems.class));
    }

    @Test
    void addToCart_WithExistingItem_UpdateQuantity() {
        // Arrange
        when(productService.getProductEntityById(1)).thenReturn(testProduct);
        when(cartsService.createCarts()).thenReturn(testCart);
        when(cartItemsRepository.findByCartIdAndProductIdAndOptionValues(eq(1), eq(1), isNull(), eq(0L)))
                .thenReturn(Optional.of(testCartItem));
        when(cartItemsRepository.save(any(CartItems.class))).thenReturn(testCartItem);
        when(cartItemMapper.convertEntityToDTO(any(CartItems.class))).thenReturn(testCartItemDTO);

        // Act
        CartItemDTO result = cartItemsService.addToCart(createForm);

        // Assert
        assertNotNull(result);
        verify(cartItemsRepository).save(testCartItem);
    }

    @Test
    void addToCart_ExceedsStock_ThrowsException() {
        // Arrange
        testProduct.setQuantity(1); // Chỉ còn 1 sản phẩm
        createForm.setQuantity(5); // Muốn mua 5

        when(productService.getProductEntityById(1)).thenReturn(testProduct);
        when(cartsService.createCarts()).thenReturn(testCart);
        when(cartItemsRepository.findByCartIdAndProductIdAndOptionValues(eq(1), eq(1), isNull(), eq(0L)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomException.class, () -> cartItemsService.addToCart(createForm));
    }
}
