package com.astitva.zomatoBackend.ZomatoApp.service.OrderItem.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.astitva.zomatoBackend.ZomatoApp.dto.OrderItemResponse;
import com.astitva.zomatoBackend.ZomatoApp.entities.OrderItem;
import com.astitva.zomatoBackend.ZomatoApp.exception.ResourceNotFoundException;
import com.astitva.zomatoBackend.ZomatoApp.repository.OrderItemRepository;
import com.astitva.zomatoBackend.ZomatoApp.service.OrderItem.OrderItemService;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    /**
     * Helper: Convert OrderItem to OrderItemResponse
     */
    private OrderItemResponse convertToResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setOrderId(item.getOrder().getId());
        response.setMenuItemId(item.getMenuItem().getId());
        response.setMenuItemName(item.getMenuItem().getName());
        response.setMenuItemDescription(item.getMenuItem().getDescription());
        response.setMenuItemCategory(item.getMenuItem().getCategory());
        response.setQuantity(item.getQuantity());
        response.setPriceAtPurchase(item.getPriceAtPurchase());
        response.setItemTotal(item.getPriceAtPurchase() * item.getQuantity());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponse> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemResponse getOrderItemById(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        return convertToResponse(orderItem);
    }
}
