package com.sparkminds.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sparkminds.ecommerce.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByIsActiveTrueAndCategoryIsActiveTrue();

    Optional<Product> findByIdAndIsActiveTrueAndCategoryIsActiveTrue(Long id);
}
