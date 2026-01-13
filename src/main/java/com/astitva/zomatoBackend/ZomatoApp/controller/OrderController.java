package com.astitva.zomatoBackend.ZomatoApp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.astitva.zomatoBackend.ZomatoApp.dto.OrderResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.CreateOrderRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.DeleteResponse;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.UserRole;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.OrderStatus;
import com.astitva.zomatoBackend.ZomatoApp.exception.UnauthorizedException;
import com.astitva.zomatoBackend.ZomatoApp.service.order.OrderService;
import com.astitva.zomatoBackend.ZomatoApp.service.OrderItem.OrderItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    /**
     * POST /api/orders
     * Create a new order
     * Authorization: Any authenticated user (CUSTOMER)
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody @Valid CreateOrderRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        // ✅ AUTHORIZATION: Verify user can only create orders for themselves
        if (!request.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You can only create orders for yourself");
        }

        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }
}
