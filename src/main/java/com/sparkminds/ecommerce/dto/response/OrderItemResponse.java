package com.sparkminds.ecommerce.dto.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
}
