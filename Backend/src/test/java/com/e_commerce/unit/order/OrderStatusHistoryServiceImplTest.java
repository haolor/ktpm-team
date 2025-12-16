package com.e_commerce.unit.order;

import com.e_commerce.dto.order.orderStatusHistoryDTO.OrderStatusHistoryDTO;
import com.e_commerce.entity.order.OrderStatusHistory;
import com.e_commerce.entity.order.Orders;
import com.e_commerce.enums.OrderStatus;
import com.e_commerce.mapper.order.OrderStatusHistoryMapper;
import com.e_commerce.repository.order.OrderStatusHistoryRepository;
import com.e_commerce.service.order.impl.OrderStatusHistoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderStatusHistoryServiceImplTest {

    @Mock
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Mock
    private OrderStatusHistoryMapper orderStatusHistoryMapper;

    @InjectMocks
    private OrderStatusHistoryServiceImpl orderStatusHistoryService;

    @Test
    void getHistoryByOrderId_returnsMappedList() {
        List<OrderStatusHistory> entities = List.of(new OrderStatusHistory());
        List<OrderStatusHistoryDTO> dtos = List.of(new OrderStatusHistoryDTO());

        when(orderStatusHistoryRepository.findByOrder_Id(1)).thenReturn(entities);
        when(orderStatusHistoryMapper.convertPageToList(entities)).thenReturn(dtos);

        List<OrderStatusHistoryDTO> result = orderStatusHistoryService.getHistoryByOrderId(1);

        assertThat(result).isEqualTo(dtos);
        verify(orderStatusHistoryRepository).findByOrder_Id(1);
        verify(orderStatusHistoryMapper).convertPageToList(entities);
    }

    @Test
    void save_persistsStatusWithOrderData() {
        Orders order = new Orders();
        order.setId(10);
        order.setOrderStatus(OrderStatus.CONFIRMED);

        when(orderStatusHistoryRepository.save(any(OrderStatusHistory.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        orderStatusHistoryService.save(order, "note");

        ArgumentCaptor<OrderStatusHistory> captor = ArgumentCaptor.forClass(OrderStatusHistory.class);
        verify(orderStatusHistoryRepository).save(captor.capture());
        OrderStatusHistory saved = captor.getValue();
        assertThat(saved.getOrder()).isEqualTo(order);
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(orderStatusHistoryMapper).convertEntityToDTO(saved);
    }
}
