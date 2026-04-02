package com.sparkminds.ecommerce.service;

import java.math.BigDecimal;
import java.util.List;

import com.sparkminds.ecommerce.dto.request.CreateProductRequest;
import com.sparkminds.ecommerce.dto.request.UpdateProductRequest;
import com.sparkminds.ecommerce.dto.response.PageData;
import com.sparkminds.ecommerce.dto.response.ProductResponse;
import com.sparkminds.ecommerce.entity.Product;

public interface ProductService {
    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long id);

    void createNewProduct(CreateProductRequest productRequest);

    void updateProduct(Long id, UpdateProductRequest productRequest);

    void deleteProduct(Long id);

    public Product findProductById(Long id);

    PageData<ProductResponse> getProductsWithFilters(
            int page, int size, String keyword, Long categoryId,
            BigDecimal minPrice, BigDecimal maxPrice, String sortBy, String direction);
}

