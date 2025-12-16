package com.e_commerce.unit.product;

import com.e_commerce.dto.product.productDTO.*;
import com.e_commerce.entity.product.Category;
import com.e_commerce.entity.product.Product;
import com.e_commerce.enums.AvailabilityStatus;
import com.e_commerce.exceptions.CustomException;
import com.e_commerce.mapper.product.ProductMapper;
import com.e_commerce.orther.CloudinaryService;
import com.e_commerce.repository.product.ProductRepository;
import com.e_commerce.service.product.CategoryService;
import com.e_commerce.service.product.OptionsGroupService;
import com.e_commerce.service.product.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private OptionsGroupService optionsGroupService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private ProductDTO testProductDTO;
    private ProductUserViewDTO testProductUserViewDTO;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1);
        testCategory.setName("Electronics");

        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPriceBase(BigDecimal.valueOf(100));
        testProduct.setQuantity(50);
        testProduct.setStatus(AvailabilityStatus.ACTIVE);
        testProduct.setCategory(testCategory);
        testProduct.setImgMain("http://example.com/image.jpg");

        testProductDTO = new ProductDTO();
        testProductDTO.setId(1);
        testProductDTO.setName("Test Product");

        testProductUserViewDTO = new ProductUserViewDTO();
        testProductUserViewDTO.setId(1);
        testProductUserViewDTO.setName("Test Product");
    }

    @Test
    void getProductById_Success() {
        // Arrange
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));
        when(productMapper.toProductUserViewDTO(any(Product.class))).thenReturn(testProductUserViewDTO);

        // Act
        ProductUserViewDTO result = productService.getProductById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(productRepository).findById(1);
    }

    @Test
    void getProductEntityById_Success() {
        // Arrange
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));

        // Act
        Product result = productService.getProductEntityById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Product", result.getName());
    }

    @Test
    void getProductEntityById_NotFound_ThrowsException() {
        // Arrange
        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomException.class, () -> productService.getProductEntityById(999));
    }

    @Test
    void createProduct_WithImage_Success() {
        // Arrange
        ProductCreateDTO createDTO = new ProductCreateDTO();
        createDTO.setName("New Product");
        createDTO.setDescription("New Description");
        createDTO.setPriceBase(BigDecimal.valueOf(200));
        createDTO.setCategoryId(1);
        
        MockMultipartFile mockFile = new MockMultipartFile(
            "image", "test.jpg", "image/jpeg", "test image content".getBytes()
        );
        createDTO.setImgMain(mockFile);

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("url", "http://cloudinary.com/uploaded.jpg");

        when(productMapper.covertCreateDTOToEntity(any(ProductCreateDTO.class))).thenReturn(testProduct);
        when(categoryService.getCategoryEntityById(anyInt())).thenReturn(testCategory);
        when(cloudinaryService.uploadFile(any(), anyString())).thenReturn(uploadResult);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.covertEntityToDTO(any(Product.class))).thenReturn(testProductDTO);

        // Act
        ProductDTO result = productService.createProduct(createDTO);

        // Assert
        assertNotNull(result);
        verify(cloudinaryService).uploadFile(any(), eq("product"));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_WithoutImage_Success() {
        // Arrange
        ProductCreateDTO createDTO = new ProductCreateDTO();
        createDTO.setName("New Product");
        createDTO.setDescription("New Description");
        createDTO.setPriceBase(BigDecimal.valueOf(200));
        createDTO.setCategoryId(1);
        createDTO.setImgMain(null);

        when(productMapper.covertCreateDTOToEntity(any(ProductCreateDTO.class))).thenReturn(testProduct);
        when(categoryService.getCategoryEntityById(anyInt())).thenReturn(testCategory);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.covertEntityToDTO(any(Product.class))).thenReturn(testProductDTO);

        // Act
        ProductDTO result = productService.createProduct(createDTO);

        // Assert
        assertNotNull(result);
        verify(cloudinaryService, never()).uploadFile(any(), anyString());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_Success() {
        // Arrange
        ProductUpdateDTO updateDTO = new ProductUpdateDTO();
        updateDTO.setName("Updated Product");
        updateDTO.setDescription("Updated Description");
        updateDTO.setStatus(AvailabilityStatus.HIDDEN);

        when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.covertEntityToDTO(any(Product.class))).thenReturn(testProductDTO);

        // Act
        ProductDTO result = productService.updateProduct(1, updateDTO);

        // Assert
        assertNotNull(result);
        verify(productRepository).findById(1);
        verify(productRepository).save(testProduct);
    }

    @Test
    void updateProduct_WithImageReplacement_Success() {
        // Arrange
        ProductUpdateDTO updateDTO = new ProductUpdateDTO();
        updateDTO.setName("Updated Product");
        updateDTO.setStatus(AvailabilityStatus.ACTIVE);
        
        MockMultipartFile mockFile = new MockMultipartFile(
            "image", "new.jpg", "image/jpeg", "new image content".getBytes()
        );
        updateDTO.setImage(mockFile);

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("url", "http://cloudinary.com/new-uploaded.jpg");

        when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));
        when(cloudinaryService.extractPublicId(anyString())).thenReturn("old_public_id");
        when(cloudinaryService.deleteFile(anyString())).thenReturn(null);
        when(cloudinaryService.uploadFile(any(), anyString())).thenReturn(uploadResult);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.covertEntityToDTO(any(Product.class))).thenReturn(testProductDTO);

        // Act
        ProductDTO result = productService.updateProduct(1, updateDTO);

        // Assert
        assertNotNull(result);
        verify(cloudinaryService).deleteFile(anyString());
        verify(cloudinaryService).uploadFile(any(), eq("product"));
        verify(productRepository).save(testProduct);
    }
}
