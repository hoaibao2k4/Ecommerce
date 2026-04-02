package com.sparkminds.ecommerce.util.mapper;

import java.util.List;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import com.sparkminds.ecommerce.dto.request.CreateProductRequest;
import com.sparkminds.ecommerce.dto.request.UpdateProductRequest;
import com.sparkminds.ecommerce.dto.response.PageData;
import com.sparkminds.ecommerce.dto.response.ProductResponse;
import com.sparkminds.ecommerce.entity.Product;

// uses = embedded class
@Mapper(componentModel = "spring", uses = {
        CategoryMapper.class }, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    @Condition
    default boolean isNotEmpty(String value) {
        // mapping if text not empty and has letter
        return StringUtils.hasText(value);
    }

    // ???? now it just sets id to product (not obj)
    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "isActive", constant = "true")
    Product toProduct(CreateProductRequest productRequest);

    // mapper automaticallyy calls when it see category entity to transfer to
    // category response
    ProductResponse toProductResponse(Product product);

    List<ProductResponse> toProductResponses(List<Product> products);

    // mapping target helps update only fields changing from client
    void updateProductFromRequest(UpdateProductRequest productRequest, @MappingTarget Product product);

    // mapstruct auto map toProductResponses
    @Mapping(source = "size", target = "pageSize")
    @Mapping(source = "number", target = "currentPage")
    PageData<ProductResponse> toPageData(Page<Product> page);
}
