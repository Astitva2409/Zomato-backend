package com.astitva.zomatoBackend.ZomatoApp.dto;

import com.astitva.zomatoBackend.ZomatoApp.entities.enums.RestaurantStatus;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.RestaurantType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantBasicDTO {
    private Long id;
    private String name;
    private String city;
    private Double rating;
    private RestaurantType type;
    private RestaurantStatus restaurantStatus;
}
