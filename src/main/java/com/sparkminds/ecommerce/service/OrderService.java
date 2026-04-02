package com.sparkminds.ecommerce.service;

import java.math.BigDecimal;
import java.util.List;

import com.sparkminds.ecommerce.dto.request.CreateOrderRequest;
import com.sparkminds.ecommerce.dto.response.OrderResponse;
import com.sparkminds.ecommerce.dto.response.PageData;
import com.sparkminds.ecommerce.enumerator.OrderStatus;

public interface OrderService {
    public void createOrder(CreateOrderRequest createOrderRequest);

    public List<OrderResponse> listOwnOrder();

    public OrderResponse getOrderById(Long id);

    public void cancelOrder(Long id);

    public void updateOrderStatus(Long id, OrderStatus orderStatus);

    public PageData<OrderResponse> getOrderWithFilters(int page, int size, OrderStatus status,
            BigDecimal minTotalAmount, BigDecimal maxTotalAmount,
            String sortBy, String direction);
}
