package com.sparkminds.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparkminds.ecommerce.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByIsActiveTrue();
    Optional<Category> findByIdAndIsActiveTrue(Long id);
}
