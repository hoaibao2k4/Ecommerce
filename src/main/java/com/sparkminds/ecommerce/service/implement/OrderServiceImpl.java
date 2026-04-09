package com.sparkminds.ecommerce.service.implement;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparkminds.ecommerce.dto.request.CreateItemsRequest;
import com.sparkminds.ecommerce.dto.request.CreateOrderRequest;
import com.sparkminds.ecommerce.dto.response.OrderResponse;
import com.sparkminds.ecommerce.dto.response.PageData;
import com.sparkminds.ecommerce.entity.Order;
import com.sparkminds.ecommerce.entity.OrderItem;
import com.sparkminds.ecommerce.entity.Product;
import com.sparkminds.ecommerce.entity.User;
import com.sparkminds.ecommerce.enumerator.OrderStatus;
import com.sparkminds.ecommerce.enumerator.Role;
import com.sparkminds.ecommerce.exception.BadRequestException;
import com.sparkminds.ecommerce.exception.BusinessException;
import com.sparkminds.ecommerce.exception.ResourceNotFoundException;
import com.sparkminds.ecommerce.repository.OrderRepository;
import com.sparkminds.ecommerce.service.OrderService;
import com.sparkminds.ecommerce.service.ProductService;
import com.sparkminds.ecommerce.service.UserService;
import com.sparkminds.ecommerce.util.mapper.OrderMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService iProduct;
    private final UserService iUser;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public void createOrder(CreateOrderRequest createOrderRequest) {
        User user = iUser.getCurrentUser();
        Order order = new Order();
        BigDecimal totalAmount = BigDecimal.ZERO;
        Map<Long, Integer> newItemLists = new HashMap<>();

        for (CreateItemsRequest item : createOrderRequest.getItems()) {
            if (newItemLists.containsKey(item.getProductId())) {
                newItemLists.put(item.getProductId(), newItemLists.get(item.getProductId()) + item.getQuantity());
            } else {
                newItemLists.put(item.getProductId(), item.getQuantity());
            }
        }

        for (Map.Entry<Long, Integer> entry : newItemLists.entrySet()) {
            Product product = iProduct.findProductById(entry.getKey());
            if (product.getStockQuantity() < entry.getValue()) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .order(order)
                    .quantity(entry.getValue())
                    .unitPrice(product.getPrice())
                    .build();
            BigDecimal quantity = BigDecimal.valueOf(orderItem.getQuantity());
            BigDecimal itemTotal = quantity.multiply(orderItem.getUnitPrice());
            totalAmount = totalAmount.add(itemTotal);
            order.getOrderItems().add(orderItem);
            product.setStockQuantity(product.getStockQuantity() - entry.getValue());
        }

        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
    }

    @Override
    public List<OrderResponse> listOwnOrder() {
        User user = iUser.getCurrentUser();
        return orderMapper.toOrderResponses(orderRepository.findByUser(user));
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        return orderMapper.toOrderResponse(getValidationOrder(id));

    }

    public Order findOrderById(Long id) {
        if (id == null) {
            throw new BadRequestException("Order Id is required");
        }
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        // get own order
        User user = iUser.getCurrentUser();
        Order order = getValidationOrder(id);

        // check condition: admin can cancel PENDING or CONFIRMED, user can only cancel
        // PENDING
        if (user.getRole() == Role.ADMIN) {
            if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
                throw new BusinessException("Admin can only cancel PENDING or CONFIRMED");
            }
        } else {
            if (order.getStatus() != OrderStatus.PENDING) {
                throw new BusinessException("User can only cancel PENDING");
            }
        }
        // update status
        order.setStatus(OrderStatus.CANCELLED);
        restoreStock(order);
        orderRepository.save(order);

    }

    @Override
    @Transactional
    public void updateOrderStatus(Long id, OrderStatus orderStatus) {
        // get own order
        Order order = getValidationOrder(id);
        User user = iUser.getCurrentUser();

        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Access Denied: You do not have permission");
        }

        // check condition sequence status: PENDING -> CONFIRMED -> SHIPPED -> DELIVERED
        // or CANCELLED
        if (!order.getStatus().canTransitionTo(orderStatus)) {
            throw new BusinessException("Invalid status transition from " + order.getStatus() + " to " + orderStatus);
        }

        // if transition to CANCELLED, restore stock
        if (orderStatus == OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        order.setStatus(orderStatus);
        orderRepository.save(order);

    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            item.getProduct().setStockQuantity(item.getProduct().getStockQuantity() + item.getQuantity());
        }
    }

    public Order getValidationOrder(Long id) {
        Order order = findOrderById(id);
        User user = iUser.getCurrentUser();

        if (user.getRole() != Role.ADMIN && !order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access Denied: You do not have permission");
        }
        return order;
    }

    @Override
    public PageData<OrderResponse> getOrderWithFilters(int page, int size, OrderStatus status,
            BigDecimal minTotalAmount, BigDecimal maxTotalAmount,
            String sortBy, String direction) {
        // pageable
        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orderPage = orderRepository.filterOrders(status, minTotalAmount, maxTotalAmount, pageable);

        return orderMapper.toPageData(orderPage);
    }

}
