package com.astitva.zomatoBackend.ZomatoApp.dto;

import com.astitva.zomatoBackend.ZomatoApp.entities.enums.MenuCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long orderId;

    // Menu item details
    private Long menuItemId;
    private String menuItemName;
    private String menuItemDescription;
    private MenuCategory menuItemCategory;

    // Order item specifics
    private Integer quantity;
    private Double priceAtPurchase;  // Price snapshot at time of ordering
    private Double itemTotal;         // priceAtPurchase × quantity

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}