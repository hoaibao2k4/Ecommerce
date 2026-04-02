package com.sparkminds.ecommerce.service;

import java.util.List;

import com.sparkminds.ecommerce.dto.request.CreateCategoryRequest;
import com.sparkminds.ecommerce.dto.response.CategoryResponse;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    void createNewCategory(CreateCategoryRequest categoryRequest);
    void deleteCategory(Long id);
}
