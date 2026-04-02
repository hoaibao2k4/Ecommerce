package com.sparkminds.ecommerce.service.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.sparkminds.ecommerce.dto.request.CreateItemsRequest;
import com.sparkminds.ecommerce.dto.request.CreateOrderRequest;
import com.sparkminds.ecommerce.entity.Order;
import com.sparkminds.ecommerce.entity.OrderItem;
import com.sparkminds.ecommerce.entity.Product;
import com.sparkminds.ecommerce.entity.User;
import com.sparkminds.ecommerce.enumerator.OrderStatus;
import com.sparkminds.ecommerce.enumerator.Role;
import com.sparkminds.ecommerce.exception.BusinessException;
import com.sparkminds.ecommerce.repository.OrderRepository;
import com.sparkminds.ecommerce.service.ProductService;
import com.sparkminds.ecommerce.service.UserService;
import com.sparkminds.ecommerce.util.mapper.OrderMapper;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService iUser;

    @Mock
    private ProductService iProduct;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User mockUser;
    private User otherUser;
    private User adminUser;
    private Product mockProduct;
    private Order mockOrder;

    // set up data
    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setRole(Role.USER);

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setRole(Role.USER);

        adminUser = new User();
        adminUser.setId(3L);
        adminUser.setRole(Role.ADMIN);

        mockProduct = new Product();
        mockProduct.setId(11L);
        mockProduct.setName("Product 1");
        mockProduct.setPrice(BigDecimal.valueOf(100.0));
        mockProduct.setStockQuantity(8);

        mockOrder = new Order();
        mockOrder.setId(12L);
        mockOrder.setOrderItems(new HashSet<>());
    }

    // Test successful order placement and verify that stock is correctly deducted
    @Test
    void placeOrder_success_stockDeducted() {
        // arrange
        CreateItemsRequest createItemsRequest = CreateItemsRequest.builder()
                .productId(11L)
                .quantity(3)
                .build();
        CreateOrderRequest request = CreateOrderRequest.builder()
                .items(List.of(createItemsRequest))
                .build();
        when(iUser.getCurrentUser()).thenReturn(mockUser);
        when(iProduct.findProductById(11L)).thenReturn(mockProduct);

        // action
        orderService.createOrder(request);

        // assert
        assertEquals(5, mockProduct.getStockQuantity());
        verify(orderRepository).save(any());
    }

    // Test order placement failure when product stock is insufficient
    @Test
    void placeOrder_insufficient_throwsException() {
        // arrange
        CreateItemsRequest createItemsRequest = CreateItemsRequest.builder()
                .productId(11L)
                .quantity(10)
                .build();
        CreateOrderRequest request = CreateOrderRequest.builder()
                .items(List.of(createItemsRequest))
                .build();
        when(iUser.getCurrentUser()).thenReturn(mockUser);
        when(iProduct.findProductById(11L)).thenReturn(mockProduct);

        // action
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            orderService.createOrder(request);
        });

        // assert
        assertEquals(8, mockProduct.getStockQuantity());

        verify(orderRepository, never()).save(any());

        assertEquals("Insufficient stock for product: Product 1", ex.getMessage());
    }

    // Test that a user can successfully cancel a PENDING order and stock is restored
    @Test
    void cancelOrderUser_whenPending_stockRestored() {
        // arrange
        OrderItem orderItem = OrderItem.builder()
                .id(12L)
                .order(mockOrder)
                .product(mockProduct)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(120))
                .build();
        mockOrder.setOrderItems(new HashSet<>(List.of(orderItem)));
        mockOrder.setUser(mockUser);
        mockOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(12L)).thenReturn(Optional.of(mockOrder));
        when(iUser.getCurrentUser()).thenReturn(mockUser);

        // action
        orderService.cancelOrder(12L);

        // assert
        assertEquals(10, mockProduct.getStockQuantity());
        assertEquals(OrderStatus.CANCELLED, mockOrder.getStatus());

        verify(orderRepository).save(mockOrder);

    }

    // Test that a user cannot cancel an order that is no longer in PENDING status
    @Test
    void cancelOrderUser_whenNotPending_throwsException() {
        // arrange
        OrderItem orderItem = OrderItem.builder()
                .id(12L)
                .order(mockOrder)
                .product(mockProduct)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(120))
                .build();
        mockOrder.setOrderItems(new HashSet<>(List.of(orderItem)));
        mockOrder.setUser(mockUser);
        mockOrder.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(12L)).thenReturn(Optional.of(mockOrder));
        when(iUser.getCurrentUser()).thenReturn(mockUser);

        // action
        BusinessException ex = assertThrows(BusinessException.class, () -> orderService.cancelOrder(12L));

        // assert
        assertEquals(8, mockProduct.getStockQuantity());
        assertEquals(OrderStatus.DELIVERED, mockOrder.getStatus());
        assertEquals("User can only cancel PENDING", ex.getMessage());

        verify(orderRepository, never()).save(any());
    }

    // Test that a user cannot cancel an order belonging to another user
    @Test
    void cancelOrderUser_byOtherUser_throwsException() {
        // arrange
        OrderItem orderItem = OrderItem.builder()
                .id(12L)
                .order(mockOrder)
                .product(mockProduct)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(120))
                .build();
        mockOrder.setOrderItems(new HashSet<>(List.of(orderItem)));
        mockOrder.setUser(otherUser);
        mockOrder.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(12L)).thenReturn(Optional.of(mockOrder));
        when(iUser.getCurrentUser()).thenReturn(mockUser);

        // action
        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> orderService.cancelOrder(12L));

        // assert
        assertEquals(8, mockProduct.getStockQuantity());
        assertEquals("Access Denied: You do not have permission", ex.getMessage());
        verify(orderRepository, never()).save(any());
    }

    // Test that an admin can cancel an order in PENDING or CONFIRMED status and stock is restored
    @Test
    void cancelOrderAdmin_whenPendingOrConfirmed_stockRestored() {
        // arrange
        OrderItem orderItem = OrderItem.builder()
                .id(12L)
                .order(mockOrder)
                .product(mockProduct)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(120))
                .build();
        mockOrder.setOrderItems(new HashSet<>(List.of(orderItem)));
        mockOrder.setUser(adminUser);
        mockOrder.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(12L)).thenReturn(Optional.of(mockOrder));
        when(iUser.getCurrentUser()).thenReturn(adminUser);

        // action
        orderService.cancelOrder(12L);

        // assert
        assertEquals(10, mockProduct.getStockQuantity());
        assertEquals(OrderStatus.CANCELLED, mockOrder.getStatus());

        verify(orderRepository).save(mockOrder);
    }

    // Test that an admin cannot cancel an order that is already SHIPPED or DELIVERED
    @Test
    void cancelOrderAdmin_whenNotPendingOrConfirmed_throwsException() {
        // arrange
        OrderItem orderItem = OrderItem.builder()
                .id(12L)
                .order(mockOrder)
                .product(mockProduct)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(120))
                .build();
        mockOrder.setOrderItems(new HashSet<>(List.of(orderItem)));
        mockOrder.setUser(adminUser);
        mockOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(12L)).thenReturn(Optional.of(mockOrder));
        when(iUser.getCurrentUser()).thenReturn(adminUser);

        // action
        BusinessException ex = assertThrows(BusinessException.class, () -> orderService.cancelOrder(12L));

        // assert
        assertEquals(8, mockProduct.getStockQuantity());
        assertEquals(OrderStatus.DELIVERED, mockOrder.getStatus());
        assertEquals("Admin can only cancel PENDING or CONFIRMED", ex.getMessage());

        verify(orderRepository, never()).save(any());
    }

    // Test successful order status update by an admin
    @Test
    void updateOrderStatus_whenValidTransition_success() {
        // arrange
        mockOrder.setStatus(OrderStatus.PENDING);
        mockOrder.setUser(mockUser);
        when(orderRepository.findById(12L)).thenReturn(Optional.of(mockOrder));
        when(iUser.getCurrentUser()).thenReturn(adminUser);

        // action
        orderService.updateOrderStatus(12L, OrderStatus.CONFIRMED);

        // assert
        assertEquals(OrderStatus.CONFIRMED, mockOrder.getStatus());
        verify(orderRepository).save(mockOrder);
    }

    // Test that a BusinessException is thrown for an invalid order status transition
    @Test
    void updateOrderStatus_whenInvalidTransition_throwsException() {
        // arrange
        mockOrder.setStatus(OrderStatus.DELIVERED);
        mockOrder.setUser(mockUser);
        when(orderRepository.findById(12L)).thenReturn(Optional.of(mockOrder));
        when(iUser.getCurrentUser()).thenReturn(adminUser);

        // action
        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.updateOrderStatus(12L, OrderStatus.CONFIRMED));

        // assert
        assertEquals("Invalid status transition from DELIVERED to CONFIRMED", ex.getMessage());
        verify(orderRepository, never()).save(any());
    }

    // Test that a regular user is denied access to update any order status
    @Test
    void updateOrderStatus_byUser_throwsException() {
        // arrange
        mockOrder.setStatus(OrderStatus.CONFIRMED);
        mockOrder.setUser(mockUser);
        when(orderRepository.findById(12L)).thenReturn(Optional.of(mockOrder));
        when(iUser.getCurrentUser()).thenReturn(mockUser);

        // action
        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> orderService.updateOrderStatus(12L, OrderStatus.CONFIRMED));

        // assert
        assertEquals("Access Denied: You do not have permission", ex.getMessage());
        verify(orderRepository, never()).save(any());
    }
}