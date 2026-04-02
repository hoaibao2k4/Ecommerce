package com.sparkminds.ecommerce.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sparkminds.ecommerce.entity.Order;
import com.sparkminds.ecommerce.entity.User;
import com.sparkminds.ecommerce.enumerator.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    public List<Order> findByUser(User user);

    @Query("SELECT o from Order o " +
            "where (:status is null or o.status = :status) " +
            "and (:minTotalAmount is null or o.totalAmount >= :minTotalAmount) " +
            "and (:maxTotalAmount is null or o.totalAmount <= :maxTotalAmount)")
    Page<Order> filterOrders(@Param("status") OrderStatus status, @Param("minTotalAmount") BigDecimal minTotalAmount,
            @Param("maxTotalAmount") BigDecimal maxTotalAmount, Pageable pageable);
}
