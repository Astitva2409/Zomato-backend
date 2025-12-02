package com.astitva.zomatoBackend.ZomatoApp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequest {
    @NotNull
    private Long menuItemId;

    @NotNull
    private Integer quantity;
}
