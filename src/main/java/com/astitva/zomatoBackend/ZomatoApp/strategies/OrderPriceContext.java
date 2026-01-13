package com.astitva.zomatoBackend.ZomatoApp.strategies;

import com.astitva.zomatoBackend.ZomatoApp.entities.Restaurant;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderPriceContext {
    private Long userId;                    // NOT User object
    private Long restaurantId;              // NOT Restaurant object

    // Computed/extracted values
    private double cartValue;               // Sum of items
    private LocalDateTime orderTime;        // Order placement time
    private int totalItems;                 // Item count
    private boolean isPeakHours;            // Calculated boolean
    private String deliveryCity;            // Extracted from address

}
