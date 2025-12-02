package com.astitva.zomatoBackend.ZomatoApp.dto;

import com.astitva.zomatoBackend.ZomatoApp.entities.enums.RestaurantType;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.RestaurantStatus;
import lombok.Data;

@Data
public class RestaurantResponse {
    private Long id;
    private String name;
    private String description;
    private String city;
    private String address;
    private Double rating;
    private RestaurantType type;
    private RestaurantStatus restaurantStatus;
}
