package com.astitva.zomatoBackend.ZomatoApp.entities.enums;

public enum OrderStatus {
    PENDING,        // Order created but not confirmed
    CONFIRMED,      // Restaurant accepted
    PREPARING,      // Being prepared
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}
