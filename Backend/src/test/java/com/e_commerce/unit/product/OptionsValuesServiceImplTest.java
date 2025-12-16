package com.e_commerce.unit.product;

import com.e_commerce.dto.product.optionValuesDTO.OptionValuesCreateDTO;
import com.e_commerce.dto.product.optionValuesDTO.OptionValuesDTO;
import com.e_commerce.dto.product.optionValuesDTO.OptionValuesUpdateDTO;
import com.e_commerce.entity.product.OptionGroup;
import com.e_commerce.entity.product.OptionValues;
import com.e_commerce.enums.AvailabilityStatus;
import com.e_commerce.exceptions.CustomException;
import com.e_commerce.mapper.product.OptionsValuesMapper;
import com.e_commerce.repository.product.OptionsValuesRepository;
import com.e_commerce.service.product.OptionsGroupService;
import com.e_commerce.service.product.impl.OptionsValuesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
class OptionsValuesServiceImplTest {

    @Mock
    private OptionsValuesMapper optionsValuesMapper;
    @Mock
    private OptionsValuesRepository optionsValuesRepository;
    @Mock
    private OptionsGroupService optionsGroupService;

    @InjectMocks
    private OptionsValuesServiceImpl optionsValuesService;

    private OptionValues optionValue;
    private OptionGroup optionGroup;

    @BeforeEach
    void setUp() {
        optionGroup = new OptionGroup();
        optionGroup.setId(2);

        optionValue = new OptionValues();
        optionValue.setId(5);
        optionValue.setName("Blue");
        optionValue.setAdditionalPrice(new BigDecimal("10.00"));
        optionValue.setStockQuantity(5);
    }

    @Test
    void getVariantValueEntityById_found_returns() {
        when(optionsValuesRepository.findById(5)).thenReturn(Optional.of(optionValue));

        OptionValues result = optionsValuesService.getVariantValueEntityById(5);

        assertThat(result).isSameAs(optionValue);
    }

    @Test
    void getVariantValueEntityById_notFound_throws() {
        when(optionsValuesRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> optionsValuesService.getVariantValueEntityById(99))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void createOptionValues_setsGroupStatusAndSaves() {
        OptionValuesCreateDTO createDTO = new OptionValuesCreateDTO();
        createDTO.setOptionsGroupId(optionGroup.getId());
        createDTO.setName("Red");
        createDTO.setPrice(new BigDecimal("12.00"));
        createDTO.setStockQuantity(3);

        OptionValuesDTO dto = new OptionValuesDTO();

        when(optionsValuesMapper.convertCreateDTOToEntity(createDTO)).thenReturn(optionValue);
        when(optionsGroupService.getVariantOptionEntityById(optionGroup.getId())).thenReturn(optionGroup);
        when(optionsValuesRepository.save(any(OptionValues.class))).thenReturn(optionValue);
        when(optionsValuesMapper.convertEntityToDTO(optionValue)).thenReturn(dto);

        OptionValuesDTO result = optionsValuesService.createOptionValues(createDTO);

        assertThat(result).isSameAs(dto);
        ArgumentCaptor<OptionValues> captor = ArgumentCaptor.forClass(OptionValues.class);
        verify(optionsValuesRepository).save(captor.capture());
        OptionValues saved = captor.getValue();
        assertThat(saved.getOptionGroup()).isEqualTo(optionGroup);
        assertThat(saved.getStatus()).isEqualTo(AvailabilityStatus.ACTIVE);
    }

    @Test
    void updateVariantValue_updatesFields() {
        OptionValuesUpdateDTO updateDTO = new OptionValuesUpdateDTO();
        updateDTO.setValue("NewName");
        updateDTO.setPrice(new BigDecimal("20.00"));

        OptionValuesDTO mapped = new OptionValuesDTO();

        when(optionsValuesRepository.findById(5)).thenReturn(Optional.of(optionValue));
        when(optionsValuesRepository.save(optionValue)).thenReturn(optionValue);
        when(optionsValuesMapper.convertEntityToDTO(optionValue)).thenReturn(mapped);

        OptionValuesDTO result = optionsValuesService.updateVariantValue(updateDTO, 5);

        assertThat(result).isSameAs(mapped);
        assertThat(optionValue.getName()).isEqualTo("NewName");
        assertThat(optionValue.getAdditionalPrice()).isEqualByComparingTo("20.00");
    }

    @Test
    void getVariantValueEntitiesById_delegatesToRepo() {
        List<OptionValues> entities = List.of(optionValue);
        when(optionsValuesRepository.findAllById(List.of(1, 2))).thenReturn(entities);

        List<OptionValues> result = optionsValuesService.getVariantValueEntitiesById(List.of(1, 2));

        assertThat(result).isSameAs(entities);
        verify(optionsValuesRepository).findAllById(List.of(1, 2));
    }

    @Test
    void getVariantValuesByVariantOptionId_mapsList() {
        List<OptionValues> entities = List.of(optionValue);
        List<OptionValuesDTO> mapped = List.of(new OptionValuesDTO());

        when(optionsValuesRepository.findByVariantOptionId(3)).thenReturn(entities);
        when(optionsValuesMapper.convertPageToListDTO(entities)).thenReturn(mapped);

        List<OptionValuesDTO> result = optionsValuesService.getVariantValuesByVariantOptionId(3);

        assertThat(result).isEqualTo(mapped);
        verify(optionsValuesRepository).findByVariantOptionId(3);
    }

    @Test
    void decreaseStock_success_noException() {
        when(optionsValuesRepository.decreaseStock(5, 2)).thenReturn(1);

        optionsValuesService.decreaseStock(5, 2);

        verify(optionsValuesRepository).decreaseStock(5, 2);
    }

    @Test
    void decreaseStock_failure_throws() {
        when(optionsValuesRepository.decreaseStock(5, 2)).thenReturn(0);

        assertThatThrownBy(() -> optionsValuesService.decreaseStock(5, 2))
                .isInstanceOf(CustomException.class);
    }
}
