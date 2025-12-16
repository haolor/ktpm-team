package com.e_commerce.unit.product;

import com.e_commerce.entity.product.Category;
import com.e_commerce.exceptions.CustomException;
import com.e_commerce.repository.product.CategoryRepository;
import com.e_commerce.service.product.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1);
        testCategory.setName("Electronics");
    }

    @Test
    void getCategoryEntityById_Success() {
        // Arrange
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(testCategory));

        // Act
        Category result = categoryService.getCategoryEntityById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Electronics", result.getName());
        verify(categoryRepository).findById(1);
    }

    @Test
    void getCategoryEntityById_NotFound_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomException.class, () -> categoryService.getCategoryEntityById(999));
    }
}
