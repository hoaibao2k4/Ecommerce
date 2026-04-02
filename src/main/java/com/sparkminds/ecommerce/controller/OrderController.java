package com.sparkminds.ecommerce.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparkminds.ecommerce.dto.request.CreateOrderRequest;
import com.sparkminds.ecommerce.dto.response.OrderResponse;
import com.sparkminds.ecommerce.dto.response.PageData;
import com.sparkminds.ecommerce.enumerator.OrderStatus;
import com.sparkminds.ecommerce.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService iOrder;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        iOrder.createOrder(createOrderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderResponse>> listOwnOrder() {
        List<OrderResponse> orders = iOrder.listOwnOrder();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse order = iOrder.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        iOrder.cancelOrder(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long id,
            @RequestParam OrderStatus orderStatus) {
        iOrder.updateOrderStatus(id, orderStatus);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageData<OrderResponse>> getOrderWithFilters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) BigDecimal minTotalAmount,
            @RequestParam(required = false) BigDecimal maxTotalAmount,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        PageData<OrderResponse> orders = iOrder.getOrderWithFilters(page, size, status, minTotalAmount, maxTotalAmount,
                sortBy, direction);
        return ResponseEntity.ok(orders);
    }
}
