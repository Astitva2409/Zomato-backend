package com.astitva.zomatoBackend.ZomatoApp.service.OrderItem;

import com.astitva.zomatoBackend.ZomatoApp.dto.OrderItemResponse;
import java.util.List;

public interface OrderItemService {
    /**
     * Get all items in an order
     */
    List<OrderItemResponse> getOrderItemsByOrderId(Long orderId);

    /**
     * Get a specific order item
     */
    OrderItemResponse getOrderItemById(Long orderItemId);
}
