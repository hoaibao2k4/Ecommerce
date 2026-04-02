package com.sparkminds.ecommerce.util.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.sparkminds.ecommerce.entity.Order;

import jakarta.persistence.criteria.Predicate;

public class OrderSpec {
    private OrderSpec() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Order> filterOrders(String keyword, BigDecimal totalAmount) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                // Search by user username if keyword is provided
                predicates.add(cb.like(cb.lower(root.join("user").get("username")), '%' + keyword.toLowerCase() + '%'));
            }

            if (totalAmount != null) {
                // JPA expects field name, not column name
                predicates.add(cb.lessThanOrEqualTo(root.get("totalAmount"), totalAmount));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
