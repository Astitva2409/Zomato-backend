package com.astitva.zomatoBackend.ZomatoApp.controller;

import com.astitva.zomatoBackend.ZomatoApp.dto.CreateRestaurantRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.DeleteRestaurantResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.RestaurantResponse;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.UserRole;
import com.astitva.zomatoBackend.ZomatoApp.exception.UnauthorizedException;
import com.astitva.zomatoBackend.ZomatoApp.service.restaurant.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    /**
     * POST /api/restaurants
     * Create a new restaurant (only RESTAURANT_OWNER can create)
     * Authorization: Checked here in controller
     */
    @PostMapping("/{userId}/create-restaurant")
    public ResponseEntity<RestaurantResponse> createRestaurant(@PathVariable Long userId, @RequestBody @Valid
                                                                   CreateRestaurantRequest request,
                                                               Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        // ✅ AUTHORIZATION CHECK AT CONTROLLER LEVEL
        if (!user.getRole().contains(UserRole.RESTAURANT_OWNER)) {
            throw new UnauthorizedException("Given user is not an Owner of any restaurant");
        }

        if (!user.getId().equals(userId)) {
            throw new UnauthorizedException("Cannot create another Restaurant Owner's restaurant");
        }

        RestaurantResponse response = restaurantService.createRestaurant(request, user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/restaurants/{restaurantId}
     * Get a specific restaurant by ID
     * No authorization needed - anyone can view
     */
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(restaurantId));
    }

    /**
     * GET /api/restaurants
     * Get all restaurants with pagination
     * No authorization needed
     */
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(restaurantService.getAllRestaurants(pageable));
    }

    /**
     * PUT /api/restaurants/{restaurantId}
     * Update restaurant details
     * Authorization: Only the owner can update their restaurant
     */
    @PutMapping("/update/{restaurantId}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable Long restaurantId,
            @RequestBody @Valid Map<String, Object> restaurantUpdates,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        RestaurantResponse restaurant = restaurantService.getRestaurantById(restaurantId);

        if(!user.getId().equals(restaurant.getOwner().getId())) {
            throw new UnauthorizedException("Only restaurant owner can update the restaurant details");
        }

        return ResponseEntity.ok(restaurantService.updateRestaurant(restaurantId,restaurantUpdates));
    }

    /**
     * GET /api/restaurants/owner/{ownerId}
     * Get all restaurants owned by a specific user
     * No authorization needed - public information
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByOwner(
            @PathVariable Long ownerId) {
        List<RestaurantResponse> response = restaurantService.getRestaurantsByOwner(ownerId);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/restaurants/{restaurantId}
     * Delete a restaurant
     * Authorization: Only the owner can delete their restaurant
     */
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<DeleteRestaurantResponse> deleteRestaurant(
            @PathVariable Long restaurantId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        // ✅ AUTHORIZATION CHECK AT CONTROLLER LEVEL
        RestaurantResponse restaurantResponse = restaurantService.getRestaurantById(restaurantId);

        if (!user.getId().equals(restaurantResponse.getOwner().getId())) {
            throw new UnauthorizedException("You can only delete your own restaurants");
        }

        DeleteRestaurantResponse deleteRestaurantResponse = restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.ok(deleteRestaurantResponse);
    }
}