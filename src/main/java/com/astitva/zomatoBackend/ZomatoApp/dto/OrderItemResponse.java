package com.astitva.zomatoBackend.ZomatoApp.dto;

import lombok.Data;

@Data
public class OrderItemResponse {
    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private Double priceAtPurchase;
}