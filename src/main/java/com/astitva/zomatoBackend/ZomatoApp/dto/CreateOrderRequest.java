package com.astitva.zomatoBackend.ZomatoApp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long restaurantId;

    @NotNull
    private Long addressId;

    private List<OrderItemRequest> items;
}
