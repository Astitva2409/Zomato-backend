package com.astitva.zomatoBackend.ZomatoApp.dto;

import com.astitva.zomatoBackend.ZomatoApp.entities.enums.OrderStatus;
import com.astitva.zomatoBackend.ZomatoApp.strategies.PriceBreakdown;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private Long restaurantId;

    // Lightweight restaurant details
    private RestaurantBasicDTO restaurant;

    // Order items with details
    private List<OrderItemResponse> items;

    // Price breakdown showing all charges
    private PriceBreakdownResponse priceBreakdown;

    // Order status and payment info
    private OrderStatus orderStatus;
    private String deliveryAddress;
    private boolean isPaid;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
