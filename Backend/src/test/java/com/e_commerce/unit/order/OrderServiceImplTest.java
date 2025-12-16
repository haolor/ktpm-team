package com.e_commerce.unit.order;

import com.e_commerce.dto.order.orderDTO.OrderDTO;
import com.e_commerce.entity.account.Account;
import com.e_commerce.entity.order.Orders;
import com.e_commerce.enums.OrderStatus;
import com.e_commerce.exceptions.CustomException;
import com.e_commerce.mapper.order.OrdersMapper;
import com.e_commerce.repository.order.OrdersRepository;
import com.e_commerce.service.account.AccountService;
import com.e_commerce.service.email.EmailService;
import com.e_commerce.service.order.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrdersMapper ordersMapper;
    @Mock private OrdersRepository ordersRepository;
    @Mock private AccountService accountService;
    @Mock private EmailService emailService;
    @Mock private com.e_commerce.service.order.impl.OrderStatusHistoryServiceImpl orderStatusHistoryService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Orders order;

    @BeforeEach
    void setup() {
        Account acc = new Account();
        acc.setId(1);
        acc.setEmail("test@example.com");
        acc.setAccountName("tester");

        order = new Orders();
        order.setId(123);
        order.setAccount(acc);
        order.setOrderStatus(OrderStatus.PLACED);
        order.setTotalPrice(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("updateOrderStatus should persist and send email")
    void updateOrderStatus_sendsEmail() {
        when(ordersRepository.findById(123)).thenReturn(java.util.Optional.of(order));
        when(ordersRepository.save(any(Orders.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ordersMapper.convertEntityToDTO(any(Orders.class))).thenAnswer(inv -> {
            Orders o = inv.getArgument(0);
            OrderDTO dto = new OrderDTO();
            dto.setId(o.getId());
            dto.setOrderStatus(o.getOrderStatus().name());
            dto.setTotalPrice(o.getTotalPrice());
            return dto;
        });

        OrderDTO result = orderService.updateOrderStatus(123, OrderStatus.CONFIRMED);

        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED.name());
        verify(ordersRepository, times(2)).save(any(Orders.class));
        verify(emailService).sendOrderStatusEmail(eq(OrderStatus.CONFIRMED), anyString(), anyString(), anyString(), any());
        verify(orderStatusHistoryService).save(any(Orders.class), contains("Change status"));
    }

    @Test
    @DisplayName("adminUpdateOrderStatus rejects delivered/cancelled edits")
    void adminUpdateOrderStatus_rejectsTerminalStates() {
        order.setOrderItems(new ArrayList<>());
        order.setOrderStatus(OrderStatus.DELIVERED);
        when(ordersRepository.findById(123)).thenReturn(java.util.Optional.of(order));

        assertThatThrownBy(() -> orderService.adminUpdateOrderStatus(123, OrderStatus.CONFIRMED))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("adminUpdateOrderStatus blocks invalid transition PLACED->DELIVERED")
    void adminUpdateOrderStatus_blocksInvalidTransition() {
        order.setOrderItems(new ArrayList<>());
        order.setOrderStatus(OrderStatus.PLACED);
        when(ordersRepository.findById(123)).thenReturn(java.util.Optional.of(order));

        assertThatThrownBy(() -> orderService.adminUpdateOrderStatus(123, OrderStatus.DELIVERED))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("adminUpdateOrderStatus should validate and send email")
    void adminUpdateOrderStatus_confirmsFlow() {
        when(ordersRepository.findById(123)).thenReturn(java.util.Optional.of(order));
        when(ordersRepository.save(any(Orders.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ordersMapper.convertEntityToDTO(any(Orders.class))).thenAnswer(inv -> {
            Orders o = inv.getArgument(0);
            OrderDTO dto = new OrderDTO();
            dto.setId(o.getId());
            dto.setOrderStatus(o.getOrderStatus().name());
            dto.setTotalPrice(o.getTotalPrice());
            return dto;
        });

        order.setOrderItems(new java.util.ArrayList<>());
        OrderDTO result = orderService.adminUpdateOrderStatus(123, OrderStatus.CONFIRMED);

        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED.name());
        verify(emailService).sendOrderStatusEmail(eq(OrderStatus.CONFIRMED), anyString(), anyString(), anyString(), any());
        verify(orderStatusHistoryService).save(any(Orders.class), contains("Admin changed status"));
    }
}
