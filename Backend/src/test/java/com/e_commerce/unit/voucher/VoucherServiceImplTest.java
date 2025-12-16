package com.e_commerce.unit.voucher;

import com.e_commerce.dto.PageDTO;
import com.e_commerce.dto.voucher.VoucherCheck;
import com.e_commerce.dto.voucher.VoucherCreateForm;
import com.e_commerce.dto.voucher.VoucherDTO;
import com.e_commerce.dto.voucher.VoucherFilter;
import com.e_commerce.entity.Voucher;
import com.e_commerce.enums.VoucherType;
import com.e_commerce.exceptions.CustomException;
import com.e_commerce.mapper.voucher.VoucherMapper;
import com.e_commerce.repository.voucher.VoucherRepository;
import com.e_commerce.service.voucher.impl.VoucherServiceImpl;
import com.e_commerce.specification.VoucherSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoucherServiceImplTest {

    @Mock
    private VoucherMapper voucherMapper;
    @Mock
    private VoucherRepository voucherRepository;

    @InjectMocks
    private VoucherServiceImpl voucherService;

    private VoucherCreateForm createForm;
    private Voucher voucher;

    @BeforeEach
    void setUp() {
        createForm = new VoucherCreateForm();
        createForm.setCode("CODE10");
        createForm.setValue(10.0);
        createForm.setType(VoucherType.PERCENTAGE);
        createForm.setStartDate(LocalDateTime.now().minusDays(1));
        createForm.setEndDate(LocalDateTime.now().plusDays(1));

        voucher = new Voucher();
        voucher.setId(1);
        voucher.setCode(createForm.getCode());
        voucher.setStartDate(createForm.getStartDate());
        voucher.setEndDate(createForm.getEndDate());
        voucher.setActive(true);
    }

    @Test
    void createVoucher_whenCodeExists_throws() {
        when(voucherMapper.convertCreateDTOToEntity(createForm)).thenReturn(voucher);
        when(voucherRepository.existsByCode(voucher.getCode())).thenReturn(true);

        assertThatThrownBy(() -> voucherService.createVoucher(createForm))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void createVoucher_endDateBeforeStart_throws() {
        createForm.setEndDate(LocalDateTime.now().minusDays(2));
        voucher.setEndDate(createForm.getEndDate());
        when(voucherMapper.convertCreateDTOToEntity(createForm)).thenReturn(voucher);
        when(voucherRepository.existsByCode(voucher.getCode())).thenReturn(false);

        assertThatThrownBy(() -> voucherService.createVoucher(createForm))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void createVoucher_percentage_setsPercentAndSaves() {
        VoucherDTO dto = new VoucherDTO();
        when(voucherMapper.convertCreateDTOToEntity(createForm)).thenReturn(voucher);
        when(voucherRepository.existsByCode(voucher.getCode())).thenReturn(false);
        when(voucherRepository.save(voucher)).thenReturn(voucher);
        when(voucherMapper.convertEntityToDTO(voucher)).thenReturn(dto);

        VoucherDTO result = voucherService.createVoucher(createForm);

        assertThat(result).isSameAs(dto);
        assertThat(voucher.getPercent()).isEqualTo(createForm.getValue());
        assertThat(voucher.getAmount()).isNull();
        verify(voucherRepository).save(voucher);
    }

    @Test
    void createVoucher_fixedAmount_setsAmountAndSaves() {
        createForm.setType(VoucherType.FIXED_AMOUNT);
        VoucherDTO dto = new VoucherDTO();
        when(voucherMapper.convertCreateDTOToEntity(createForm)).thenReturn(voucher);
        when(voucherRepository.existsByCode(voucher.getCode())).thenReturn(false);
        when(voucherRepository.save(voucher)).thenReturn(voucher);
        when(voucherMapper.convertEntityToDTO(voucher)).thenReturn(dto);

        VoucherDTO result = voucherService.createVoucher(createForm);

        assertThat(result).isSameAs(dto);
        assertThat(voucher.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(createForm.getValue()));
        assertThat(voucher.getPercent()).isNull();
    }

    @Test
    void getVoucherEntityById_found_returns() {
        when(voucherRepository.findById(1)).thenReturn(Optional.of(voucher));

        Voucher result = voucherService.getVoucherEntityById(1);

        assertThat(result).isSameAs(voucher);
    }

    @Test
    void getVoucherEntityById_notFound_throws() {
        when(voucherRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> voucherService.getVoucherEntityById(99))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void deleteVoucher_fetchesThenDeletes() {
        when(voucherRepository.findById(1)).thenReturn(Optional.of(voucher));

        voucherService.deleteVoucher(1);

        verify(voucherRepository).delete(voucher);
    }

    @Test
    void getAllVouchers_buildsSpecAndMapsPage() {
        VoucherFilter filter = new VoucherFilter();
        Page<Voucher> page = new PageImpl<>(List.of(voucher));
        PageDTO<VoucherDTO> dtoPage = new PageDTO<>();

        when(voucherRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(voucherMapper.convertEntityPageToDTOPage(page)).thenReturn(dtoPage);

        PageDTO<VoucherDTO> result = voucherService.getAllVouchers(1, 10, filter);

        assertThat(result).isSameAs(dtoPage);
    }

    @Test
    void checkVoucher_notFound_throws() {
        when(voucherRepository.findByCode(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> voucherService.checkVoucher("NOPE"))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void checkVoucher_inactive_returnsInvalid() {
        voucher.setActive(false);
        when(voucherRepository.findByCode(voucher.getCode())).thenReturn(Optional.of(voucher));

        VoucherCheck result = voucherService.checkVoucher(voucher.getCode());

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("inactive");
    }

    @Test
    void checkVoucher_notStarted_returnsInvalid() {
        voucher.setStartDate(LocalDateTime.now().plusDays(1));
        when(voucherRepository.findByCode(voucher.getCode())).thenReturn(Optional.of(voucher));

        VoucherCheck result = voucherService.checkVoucher(voucher.getCode());

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("not valid yet");
    }

    @Test
    void checkVoucher_expired_returnsInvalid() {
        voucher.setEndDate(LocalDateTime.now().minusDays(1));
        when(voucherRepository.findByCode(voucher.getCode())).thenReturn(Optional.of(voucher));

        VoucherCheck result = voucherService.checkVoucher(voucher.getCode());

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("expired");
    }

    @Test
    void checkVoucher_activeWithinRange_returnsValidWithDto() {
        voucher.setStartDate(LocalDateTime.now().minusDays(1));
        voucher.setEndDate(LocalDateTime.now().plusDays(1));
        VoucherDTO dto = new VoucherDTO();
        when(voucherRepository.findByCode(voucher.getCode())).thenReturn(Optional.of(voucher));
        when(voucherMapper.convertEntityToDTO(voucher)).thenReturn(dto);

        VoucherCheck result = voucherService.checkVoucher(voucher.getCode());

        assertThat(result.isValid()).isTrue();
        assertThat(result.getVoucher()).isSameAs(dto);
    }
}
