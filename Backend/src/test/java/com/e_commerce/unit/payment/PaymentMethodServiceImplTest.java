package com.e_commerce.unit.payment;

import com.e_commerce.dto.payment.PaymentMethodDTO.PaymentMethodCreateDTO;
import com.e_commerce.dto.payment.PaymentMethodDTO.PaymentMethodDTO;
import com.e_commerce.entity.payment.PaymentMethod;
import com.e_commerce.exceptions.CustomException;
import com.e_commerce.mapper.payment.PaymentMethodMapper;
import com.e_commerce.repository.payment.PaymentMethodRepository;
import com.e_commerce.service.payment.impl.PaymentMethodServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceImplTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private PaymentMethodMapper paymentMethodMapper;

    @InjectMocks
    private PaymentMethodServiceImpl paymentMethodService;

    private PaymentMethod paymentMethod;
    private PaymentMethodCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        paymentMethod = new PaymentMethod();
        paymentMethod.setId(10);
        paymentMethod.setName("COD");
        paymentMethod.setCode("COD");
        paymentMethod.setDescription("cash on delivery");
        paymentMethod.setIsActive(true);

        createDTO = new PaymentMethodCreateDTO();
        createDTO.setName("New Name");
        createDTO.setCode("NEW");
        createDTO.setDescription("desc");
        createDTO.setIsActive(true);
    }

    @Test
    void getAllPaymentMethods_returnsMappedList() {
        List<PaymentMethod> methods = List.of(paymentMethod);
        List<PaymentMethodDTO> mapped = List.of(new PaymentMethodDTO());

        when(paymentMethodRepository.findAll()).thenReturn(methods);
        when(paymentMethodMapper.convertPageToListDTO(methods)).thenReturn(mapped);

        List<PaymentMethodDTO> result = paymentMethodService.getAllPaymentMethods();

        assertThat(result).isEqualTo(mapped);
        verify(paymentMethodRepository).findAll();
        verify(paymentMethodMapper).convertPageToListDTO(methods);
    }

    @Test
    void getPaymentMethodEntityById_notFound_throws() {
        when(paymentMethodRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentMethodService.getPaymentMethodEntityById(99))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void getPaymentMethodEntityById_found_returnsEntity() {
        when(paymentMethodRepository.findById(10)).thenReturn(Optional.of(paymentMethod));

        PaymentMethod result = paymentMethodService.getPaymentMethodEntityById(10);

        assertThat(result).isSameAs(paymentMethod);
        verify(paymentMethodRepository).findById(10);
    }

    @Test
    void createPaymentMethod_convertsAndSaves() {
        PaymentMethodDTO dto = new PaymentMethodDTO();

        when(paymentMethodMapper.convertCreateDTOToEntity(createDTO)).thenReturn(paymentMethod);
        when(paymentMethodRepository.save(paymentMethod)).thenReturn(paymentMethod);
        when(paymentMethodMapper.convertEntityToDTO(paymentMethod)).thenReturn(dto);

        PaymentMethodDTO result = paymentMethodService.createPaymentMethod(createDTO);

        assertThat(result).isSameAs(dto);
        verify(paymentMethodMapper).convertCreateDTOToEntity(createDTO);
        verify(paymentMethodRepository).save(paymentMethod);
        verify(paymentMethodMapper).convertEntityToDTO(paymentMethod);
    }

    @Test
    void updatePaymentMethod_updatesNonNullFieldsAndSaves() {
        PaymentMethodDTO dto = new PaymentMethodDTO();

        when(paymentMethodRepository.findById(10)).thenReturn(Optional.of(paymentMethod));
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenAnswer(inv -> inv.getArgument(0));
        when(paymentMethodMapper.convertEntityToDTO(any(PaymentMethod.class))).thenReturn(dto);

        PaymentMethodDTO result = paymentMethodService.updatePaymentMethod(10, createDTO);

        assertThat(result).isSameAs(dto);
        assertThat(paymentMethod.getName()).isEqualTo(createDTO.getName());
        assertThat(paymentMethod.getCode()).isEqualTo(createDTO.getCode());
        assertThat(paymentMethod.getDescription()).isEqualTo(createDTO.getDescription());
        assertThat(paymentMethod.getIsActive()).isEqualTo(createDTO.getIsActive());
        verify(paymentMethodRepository).save(paymentMethod);
    }
}
