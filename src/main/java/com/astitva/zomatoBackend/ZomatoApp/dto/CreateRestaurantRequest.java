package com.astitva.zomatoBackend.ZomatoApp.dto;

import com.astitva.zomatoBackend.ZomatoApp.entities.enums.RestaurantStatus;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.RestaurantType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRestaurantRequest {

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String city;

    @NotBlank
    private String address;

    private RestaurantType type;

    private RestaurantStatus restaurantStatus;
}