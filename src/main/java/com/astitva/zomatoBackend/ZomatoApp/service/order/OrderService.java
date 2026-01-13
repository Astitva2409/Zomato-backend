package com.astitva.zomatoBackend.ZomatoApp.service.order;

import com.astitva.zomatoBackend.ZomatoApp.dto.OrderResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.CreateOrderRequest;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.OrderStatus;
import java.util.List;
import java.util.Map;

public interface OrderService {
    /**
     * Create a new order with items
     * Calculates total with all charges
     * Creates Payment record (PENDING status)
     */
    OrderResponse createOrder(CreateOrderRequest request);

    /**
     * Get order by ID
     */
    OrderResponse getOrderById(Long orderId);

    /**
     * Get all orders placed by a user
     */
    List<OrderResponse> getOrdersByUser(Long userId);

    /**
     * Get all orders for a restaurant
     */
    List<OrderResponse> getOrdersByRestaurant(Long restaurantId);

    /**
     * Update order status
     * Only certain transitions are allowed based on current status
     */
    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);

    /**
     * Update order fields dynamically (using reflection)
     * Protected fields: id, createdAt, updatedAt, user, restaurant, orderItems
     */
    OrderResponse updateOrder(Long orderId, Map<String, Object> updates);

    /**
     * Cancel an order
     * Only possible if status is PENDING or CONFIRMED
     */
    void cancelOrder(Long orderId);
}
