package com.astitva.zomatoBackend.ZomatoApp.dto;

import com.astitva.zomatoBackend.ZomatoApp.entities.enums.OrderStatus;
import lombok.Data;

import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private Long restaurantId;
    private String deliveryAddress;
    private Double totalAmount;
    private boolean isPaid;
    private OrderStatus orderStatus;
    private List<OrderItemResponse> orderItems;
}
