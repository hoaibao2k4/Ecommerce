package com.sparkminds.ecommerce.service.implement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.sparkminds.ecommerce.dto.request.CreateProductRequest;
import com.sparkminds.ecommerce.dto.request.UpdateProductRequest;
import com.sparkminds.ecommerce.dto.response.PageData;
import com.sparkminds.ecommerce.dto.response.ProductResponse;
import com.sparkminds.ecommerce.entity.Category;
import com.sparkminds.ecommerce.entity.Product;
import com.sparkminds.ecommerce.enumerator.Role;
import com.sparkminds.ecommerce.exception.BadRequestException;
import com.sparkminds.ecommerce.exception.ResourceNotFoundException;
import com.sparkminds.ecommerce.repository.ProductRepository;
import com.sparkminds.ecommerce.service.ProductService;
import com.sparkminds.ecommerce.service.UserService;
import com.sparkminds.ecommerce.util.filter.ProductSpecification;
import com.sparkminds.ecommerce.util.mapper.ProductMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryServiceImpl categoryService;
    private final UserService iUser;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productMapper.toProductResponses(productRepository.findByIsActiveTrueAndCategoryIsActiveTrue());
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return productMapper.toProductResponse(findProductById(id));
    }

    @Override
    public Product findProductById(Long id) {
        if (id == null) {
            throw new BadRequestException("Product id is required");
        }
        return productRepository.findByIdAndIsActiveTrueAndCategoryIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public void createNewProduct(CreateProductRequest productRequest) {
        if (iUser.getCurrentUser().getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Access Denied: You do not have permission");
        }

        Category category = categoryService
                .findCategoryById(productRequest.getCategoryId());
        Product product = productMapper.toProduct(productRequest);
        product.setCategory(category);
        productRepository.save(product);

    }

    @Override
    public void updateProduct(Long id, UpdateProductRequest updateProductRequest) {
        if (iUser.getCurrentUser().getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Access Denied: You do not have permission");
        }
        Product product = findProductById(id);
        productMapper.updateProductFromRequest(updateProductRequest, product);
        if (updateProductRequest.getCategoryId() != null) {
            Category category = categoryService
                    .findCategoryById(updateProductRequest.getCategoryId());
            product.setCategory(category);
        }
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        if (iUser.getCurrentUser().getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Access Denied: You do not have permission");
        }
        // set IsActive is false
        Product product = findProductById(id);
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Override
    public PageData<ProductResponse> getProductsWithFilters(
            int page, int size, String keyword, Long categoryId,
            BigDecimal minPrice, BigDecimal maxPrice, String sortBy, String direction) {

        // 1. Setup Sort & Pageable
        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // 2. Create Specification
        Specification<Product> spec = ProductSpecification.filterProducts(
                keyword, categoryId, minPrice, maxPrice);

        // 3. Query repository
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        // 4. Use mapper convert to PageData
        return productMapper.toPageData(productPage);
    }

}
