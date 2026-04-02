package com.sparkminds.ecommerce.service.implement;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sparkminds.ecommerce.dto.request.CreateCategoryRequest;
import com.sparkminds.ecommerce.dto.response.CategoryResponse;
import com.sparkminds.ecommerce.entity.Category;
import com.sparkminds.ecommerce.exception.BadRequestException;
import com.sparkminds.ecommerce.exception.ResourceNotFoundException;
import com.sparkminds.ecommerce.repository.CategoryRepository;
import com.sparkminds.ecommerce.service.CategoryService;
import com.sparkminds.ecommerce.util.mapper.CategoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryMapper.toCategoryResponses(categoryRepository.findByIsActiveTrue());
    }

    @Override
    public void createNewCategory(CreateCategoryRequest categoryRequest) {
        Category category = categoryMapper.toCategory(categoryRequest);
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = findCategoryById(id);
        category.setIsActive(false);
        categoryRepository.save(category);
    }

    public Category findCategoryById(Long id) {
        if (id == null) {
            throw new BadRequestException("Category id is required");
        }
        return categoryRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

}
