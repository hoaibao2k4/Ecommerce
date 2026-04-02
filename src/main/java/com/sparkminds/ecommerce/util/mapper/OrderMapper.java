package com.sparkminds.ecommerce.util.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import com.sparkminds.ecommerce.dto.response.OrderItemResponse;
import com.sparkminds.ecommerce.dto.response.OrderResponse;
import com.sparkminds.ecommerce.dto.response.PageData;
import com.sparkminds.ecommerce.entity.Order;
import com.sparkminds.ecommerce.entity.OrderItem;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    OrderResponse toOrderResponse(Order order);

    @Mapping(source = "user.id", target = "userId")
    List<OrderResponse> toOrderResponses(List<Order> order);

    @Mapping(source = "product.id", target = "productId")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    @Mapping(source = "size", target = "pageSize")
    @Mapping(source = "number", target = "currentPage")
    PageData<OrderResponse> toPageData(Page<Order> page);
}
