package com.sparkminds.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import com.sparkminds.ecommerce.enumerator.OrderStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Builder
@Getter
public class OrderResponse {
    private Long id;
    private Long userId;
    private String username;
    private String email;   
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private Set<OrderItemResponse> orderItems;
}
