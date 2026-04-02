package com.sparkminds.ecommerce.util.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sparkminds.ecommerce.dto.request.CreateCategoryRequest;
import com.sparkminds.ecommerce.dto.response.CategoryResponse;
import com.sparkminds.ecommerce.entity.Category;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {
    @Mapping(source = "categoryName", target = "name")
    @Mapping(target = "isActive", constant = "true") // auto set true value for isActive with constant
    @Mapping(target = "id", ignore = true) // auto ignore id
    Category toCategory(CreateCategoryRequest categoryRequest);

    @Mapping(source = "name", target = "categoryName")
    CategoryResponse toCategoryResponse(Category category);

    List<CategoryResponse> toCategoryResponses(List<Category> categories);
}
