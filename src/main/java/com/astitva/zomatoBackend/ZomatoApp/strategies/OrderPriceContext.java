package com.astitva.zomatoBackend.ZomatoApp.strategies;

import com.astitva.zomatoBackend.ZomatoApp.entities.Restaurant;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderPriceContext {

    private double cartValue;
    private User user;
    private Restaurant restaurant;
    private LocalDateTime orderTime;
    private int totalItems;
    private boolean isPeakHours;
}
