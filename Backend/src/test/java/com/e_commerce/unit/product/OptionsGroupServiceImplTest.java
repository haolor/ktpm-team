package com.e_commerce.unit.product;

import com.e_commerce.dto.product.optionGroupDTO.OptionsGroupCreateDTO;
import com.e_commerce.dto.product.optionGroupDTO.OptionsGroupDTO;
import com.e_commerce.dto.product.optionGroupDTO.OptionsGroupUpdateDTO;
import com.e_commerce.entity.product.OptionGroup;
import com.e_commerce.entity.product.Product;
import com.e_commerce.exceptions.CustomException;
import com.e_commerce.mapper.product.OptionsGroupMapper;
import com.e_commerce.repository.product.OptionsGroupRepository;
import com.e_commerce.repository.product.ProductRepository;
import com.e_commerce.service.product.impl.OptionsGroupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class OptionsGroupServiceImplTest {

    @Mock
    private OptionsGroupMapper optionsGroupMapper;
    @Mock
    private OptionsGroupRepository optionsGroupRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OptionsGroupServiceImpl optionsGroupService;

    private OptionGroup optionGroup;
    private Product product;
    private OptionsGroupCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        optionGroup = new OptionGroup();
        optionGroup.setId(1);
        optionGroup.setName("Size");

        product = new Product();
        product.setId(10);

        createDTO = new OptionsGroupCreateDTO();
        createDTO.setName("Color");
        createDTO.setProductId(product.getId());
    }

    @Test
    void getVariantOptionEntityById_found_returnsEntity() {
        when(optionsGroupRepository.findById(1)).thenReturn(Optional.of(optionGroup));

        OptionGroup result = optionsGroupService.getVariantOptionEntityById(1);

        assertThat(result).isSameAs(optionGroup);
    }

    @Test
    void getVariantOptionEntityById_notFound_throws() {
        when(optionsGroupRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> optionsGroupService.getVariantOptionEntityById(5))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void createOptionGroup_mapsSetsProductAndSaves() {
        OptionsGroupDTO dto = new OptionsGroupDTO();

        when(optionsGroupMapper.convertCreateDTOToEntity(createDTO)).thenReturn(optionGroup);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(optionsGroupRepository.save(any(OptionGroup.class))).thenReturn(optionGroup);
        when(optionsGroupMapper.convertEntityToDTO(optionGroup)).thenReturn(dto);

        OptionsGroupDTO result = optionsGroupService.createOptionGroup(createDTO);

        assertThat(result).isSameAs(dto);
        ArgumentCaptor<OptionGroup> captor = ArgumentCaptor.forClass(OptionGroup.class);
        verify(optionsGroupRepository).save(captor.capture());
        assertThat(captor.getValue().getProduct()).isEqualTo(product);
        verify(productRepository).findById(product.getId());
    }

    @Test
    void updateVariantOption_updatesNameWhenProvided() {
        OptionsGroupUpdateDTO updateDTO = new OptionsGroupUpdateDTO();
        updateDTO.setName("Updated");
        OptionsGroupDTO mapped = new OptionsGroupDTO();

        when(optionsGroupRepository.findById(1)).thenReturn(Optional.of(optionGroup));
        when(optionsGroupRepository.save(optionGroup)).thenReturn(optionGroup);
        when(optionsGroupMapper.convertEntityToDTO(optionGroup)).thenReturn(mapped);

        OptionsGroupDTO result = optionsGroupService.updateVariantOption(updateDTO, 1);

        assertThat(result).isSameAs(mapped);
        assertThat(optionGroup.getName()).isEqualTo("Updated");
        verify(optionsGroupRepository).save(optionGroup);
    }

    @Test
    void getOptionGroupsByProductId_returnsMappedList() {
        List<OptionGroup> entities = List.of(optionGroup);
        List<OptionsGroupDTO> mapped = List.of(new OptionsGroupDTO());

        when(optionsGroupRepository.findByProductCategoryId(2)).thenReturn(entities);
        when(optionsGroupMapper.convertPageToListDTO(entities)).thenReturn(mapped);

        List<OptionsGroupDTO> result = optionsGroupService.getOptionGroupsByProductId(2);

        assertThat(result).isEqualTo(mapped);
        verify(optionsGroupRepository).findByProductCategoryId(2);
    }
}
