package com.astitva.zomatoBackend.ZomatoApp.service.restaurant;

import com.astitva.zomatoBackend.ZomatoApp.dto.CreateRestaurantRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.DeleteRestaurantResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.RestaurantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface RestaurantService {

    /**
     * Create a new restaurant
     * @param request - Restaurant details
     * @param ownerId - The user creating (assumed authorized by controller)
     */
    RestaurantResponse createRestaurant(CreateRestaurantRequest request, Long ownerId);

    /**
     * Get a restaurant by ID
     */
    RestaurantResponse getRestaurantById(Long restaurantId);

    /**
     * Get all restaurants with pagination
     */
    List<RestaurantResponse> getAllRestaurants(Pageable pageable);

    /**
     * Get restaurants filtered by city
     */
    Page<RestaurantResponse> getRestaurantsByCity(String city, Pageable pageable);

    /**
     * Update restaurant details
     * @param restaurantId - Restaurant to update
     * @param request - New details
     * NOTE: Authorization check is done in controller
     */
    RestaurantResponse updateRestaurant(Long restaurantId, Map<String, Object> restaurantUpdates);

    /**
     * Delete a restaurant
     * NOTE: Authorization check is done in controller
     */
    DeleteRestaurantResponse deleteRestaurant(Long restaurantId);

    /**
     * Get restaurants owned by a specific user
     */
    List<RestaurantResponse> getRestaurantsByOwner(Long ownerId);
}
