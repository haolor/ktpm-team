package com.e_commerce.unit.order;

import com.e_commerce.dto.order.orderItemsDTO.OrderItemsCreateForm;
import com.e_commerce.dto.order.orderItemsDTO.OrderItemsDTO;
import com.e_commerce.entity.order.CartItems;
import com.e_commerce.entity.order.OrderItems;
import com.e_commerce.entity.order.Orders;
import com.e_commerce.entity.product.OptionValues;
import com.e_commerce.entity.product.Product;
import com.e_commerce.exceptions.CustomException;
import com.e_commerce.mapper.order.OrderItemMapper;
import com.e_commerce.repository.order.OrderItemsRepository;
import com.e_commerce.service.order.impl.OrderItemsServiceImpl;
import com.e_commerce.service.product.OptionsValuesService;
import com.e_commerce.service.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderItemsServiceImplTest {

    @Mock
    private OrderItemMapper orderItemMapper;
    @Mock
    private OrderItemsRepository orderItemsRepository;
    @Mock
    private ProductService productService;
    @Mock
    private OptionsValuesService optionsValuesService;

    @InjectMocks
    private OrderItemsServiceImpl orderItemsService;

    private Product product;
    private OptionValues option;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1);
        product.setPriceBase(new BigDecimal("100.00"));

        option = new OptionValues();
        option.setId(10);
        option.setName("Color");
        option.setStockQuantity(10);
        option.setAdditionalPrice(new BigDecimal("20.00"));
    }

    @Test
    void getOrderItemsEntityById_found_returnsEntity() {
        OrderItems orderItems = new OrderItems();
        when(orderItemsRepository.findById(5)).thenReturn(Optional.of(orderItems));

        OrderItems result = orderItemsService.getOrderItemsEntityById(5);

        assertThat(result).isSameAs(orderItems);
    }

    @Test
    void getOrderItemsEntityById_notFound_throws() {
        when(orderItemsRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemsService.getOrderItemsEntityById(99))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void createOrderItems_withOptions_setsUnitPriceAndSaves() {
        OrderItemsCreateForm form = new OrderItemsCreateForm();
        form.setProductId(product.getId());
        form.setQuantity(2);
        form.setNote("note");
        form.setOptionValueId(List.of(option.getId()));

        OrderItems saved = new OrderItems();
        saved.setId(123);

        when(productService.getProductEntityById(product.getId())).thenReturn(product);
        when(optionsValuesService.getVariantValueEntitiesById(form.getOptionValueId())).thenReturn(List.of(option));
        when(orderItemsRepository.save(any(OrderItems.class))).thenReturn(saved);
        when(orderItemMapper.convertEntityToDTO(saved)).thenReturn(new OrderItemsDTO());

        OrderItemsDTO result = orderItemsService.createOrderItems(form);

        assertThat(result).isNotNull();
        ArgumentCaptor<OrderItems> captor = ArgumentCaptor.forClass(OrderItems.class);
        verify(orderItemsRepository).save(captor.capture());
        OrderItems toSave = captor.getValue();
        BigDecimal expectedUnitPrice = product.getPriceBase().add(option.getAdditionalPrice());
        assertThat(toSave.getUnitPrice()).isEqualByComparingTo(expectedUnitPrice);
        assertThat(toSave.getQuantity()).isEqualTo(form.getQuantity());
    }

    @Test
    void createOrderItems_insufficientOptionStock_throws() {
        OrderItemsCreateForm form = new OrderItemsCreateForm();
        form.setProductId(product.getId());
        form.setQuantity(5);
        form.setOptionValueId(List.of(option.getId()));

        option.setStockQuantity(3);

        when(productService.getProductEntityById(product.getId())).thenReturn(product);
        when(optionsValuesService.getVariantValueEntitiesById(form.getOptionValueId())).thenReturn(List.of(option));

        assertThatThrownBy(() -> orderItemsService.createOrderItems(form))
                .isInstanceOf(CustomException.class);
        verify(orderItemsRepository, never()).save(any(OrderItems.class));
    }

    @Test
    void createOrderItemsFromCartItem_buildsFromCartAndSaves() {
        Orders order = new Orders();
        order.setId(7);

        CartItems cartItem = new CartItems();
        cartItem.setProduct(product);
        cartItem.setSelectedOptions(new ArrayList<>());
        cartItem.setQuantity(1);
        cartItem.setNote("cart note");

        List<OrderItems> stored = new ArrayList<>();
        when(orderItemsRepository.saveAll(any())).thenAnswer(inv -> {
            stored.addAll(inv.getArgument(0));
            return stored;
        });

        List<OrderItems> result = orderItemsService.createOrderItemsFromCartItem(List.of(cartItem), order);

        assertThat(result).hasSize(1);
        OrderItems created = stored.getFirst();
        assertThat(created.getOrder()).isEqualTo(order);
        assertThat(created.getProduct()).isEqualTo(product);
        assertThat(created.getQuantity()).isEqualTo(1);
    }
}
