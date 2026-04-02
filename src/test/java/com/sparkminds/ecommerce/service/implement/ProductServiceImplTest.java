package com.sparkminds.ecommerce.service.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.sparkminds.ecommerce.dto.request.CreateProductRequest;
import com.sparkminds.ecommerce.dto.request.UpdateProductRequest;
import com.sparkminds.ecommerce.entity.Category;
import com.sparkminds.ecommerce.entity.Product;
import com.sparkminds.ecommerce.entity.User;
import com.sparkminds.ecommerce.enumerator.Role;
import com.sparkminds.ecommerce.exception.BadRequestException;
import com.sparkminds.ecommerce.exception.ResourceNotFoundException;
import com.sparkminds.ecommerce.repository.ProductRepository;
import com.sparkminds.ecommerce.service.UserService;
import com.sparkminds.ecommerce.util.mapper.ProductMapper;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryServiceImpl categoryService;

    @Mock
    private UserService iUser;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product mockProduct;
    private User adminUser;
    private User regularUser;
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);

        regularUser = new User();
        regularUser.setId(2L);
        regularUser.setRole(Role.USER);

        mockCategory = new Category();
        mockCategory.setId(1L);
        mockCategory.setName("Category 1");
        mockCategory.setIsActive(true);

        mockProduct = new Product();
        mockProduct.setId(11L);
        mockProduct.setName("Product 1");
        mockProduct.setPrice(BigDecimal.valueOf(100.0));
        mockProduct.setStockQuantity(8);
        mockProduct.setIsActive(true);
        mockProduct.setCategory(mockCategory);
    }

    // Test successful product creation when performed by an admin user
    @Test
    void createNewProduct_byAdmin_success() {
        // arrange
        CreateProductRequest request = CreateProductRequest.builder()
                .categoryId(1L)
                .price(BigDecimal.valueOf(100.0))
                .build();
        when(iUser.getCurrentUser()).thenReturn(adminUser);
        when(categoryService.findCategoryById(any())).thenReturn(mockCategory);
        when(productMapper.toProduct(any())).thenReturn(mockProduct);

        // action
        productService.createNewProduct(request);

        // assert
        verify(productRepository).save(any());
    }

    // Test that a regular user is denied access to create a new product
    @Test
    void createNewProduct_byUser_throwsException() {
        // arrange
        CreateProductRequest request = new CreateProductRequest();
        when(iUser.getCurrentUser()).thenReturn(regularUser);

        // action & assert
        assertThrows(AccessDeniedException.class, () -> productService.createNewProduct(request));
        verify(productRepository, never()).save(any());
    }

    // Test successful product deletion (deactivation) when performed by an admin
    // user
    @Test
    void deleteProduct_byAdmin_productDeactivated() {
        // arrange
        when(iUser.getCurrentUser()).thenReturn(adminUser);
        when(productRepository.findByIdAndIsActiveTrueAndCategoryIsActiveTrue(11L))
                .thenReturn(Optional.of(mockProduct));

        // action
        productService.deleteProduct(11L);

        // assert
        assertEquals(false, mockProduct.getIsActive());
        verify(productRepository).save(mockProduct);
    }

    // Test that an Exception is thrown when attempting to delete a
    // non-existent product
    @Test
    void deleteProduct_productNotFound_throwsException() {
        // arrange
        when(iUser.getCurrentUser()).thenReturn(adminUser);
        when(productRepository.findByIdAndIsActiveTrueAndCategoryIsActiveTrue(11L)).thenReturn(Optional.empty());

        // action
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(11L));
        
        // assert
        assertEquals("Product not found", ex.getMessage());
        verify(productRepository, never()).save(any());
    }
    // Test successful product update when performed by an admin user
    @Test
    void updateProduct_byAdmin_success() {
        // arrange
        UpdateProductRequest request = new UpdateProductRequest();
        when(iUser.getCurrentUser()).thenReturn(adminUser);
        when(productRepository.findByIdAndIsActiveTrueAndCategoryIsActiveTrue(11L)).thenReturn(Optional.of(mockProduct));

        // action
        productService.updateProduct(11L, request);

        // assert
        verify(productMapper).updateProductFromRequest(request, mockProduct);
        verify(productRepository).save(mockProduct);
    }
}
