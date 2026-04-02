package com.sparkminds.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class UpdateProductRequest {
    private String name;

    private String description;

    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    @Min(value = 1, message = "Stock quantity must be greater than 0")
    private Integer stockQuantity;

    private Long categoryId;
}
