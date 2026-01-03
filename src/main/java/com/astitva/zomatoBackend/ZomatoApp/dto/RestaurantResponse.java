package com.astitva.zomatoBackend.ZomatoApp.dto;

import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.RestaurantStatus;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.RestaurantType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantResponse {
    private Long id;
    private String name;
    private String description;
    private String city;
    private String address;
    private Double rating;
    private UserResponse owner;
    private RestaurantType type;
    private RestaurantStatus restaurantStatus;
}
