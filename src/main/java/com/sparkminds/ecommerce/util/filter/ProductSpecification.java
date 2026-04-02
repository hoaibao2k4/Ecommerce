package com.sparkminds.ecommerce.util.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.sparkminds.ecommerce.entity.Category;
import com.sparkminds.ecommerce.entity.Product;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {
    // use private constructor to avoid memory leak when class has static method
    private ProductSpecification() {
        throw new IllegalStateException("Utility class");
    }

    // return lamda expression: root == entity, query: custom sql query, cb:
    // filtering condition
    // (like, and, ...)
    public static Specification<Product> filterProducts(String keyword, Long categoryId, BigDecimal minPrice,
            BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            // create list of conditions
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.isTrue(root.get("isActive")));
            // JOIN with CATEGORY
            // Always join to check isActive of Category
            Join<Product, Category> categoryJoin = root.join("category");
            predicates.add(criteriaBuilder.isTrue(categoryJoin.get("isActive")));

            // like(entity.field, %keyword%)
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), '%' + keyword.toLowerCase() + '%'));
            }

            // equal(entity.field, value)
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(categoryJoin.get("id"), categoryId));
            }

            // greater than or equal(entity.field, value)
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            // less than or equal(entity.field, value)
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            // merge all conditions
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
