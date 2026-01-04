package com.astitva.zomatoBackend.ZomatoApp.controller;

import com.astitva.zomatoBackend.ZomatoApp.dto.CreateMenuItemRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.DeleteResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.MenuItemResponse;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.UserRole;
import com.astitva.zomatoBackend.ZomatoApp.exception.ResourceNotFoundException;
import com.astitva.zomatoBackend.ZomatoApp.exception.UnauthorizedException;
import com.astitva.zomatoBackend.ZomatoApp.service.menuitem.MenuItemService;
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
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final RestaurantService restaurantService;

    /**
     * POST /api/restaurants/{restaurantId}/menu-items
     * Create a new menu item for a restaurant
     * Authorization: Only RESTAURANT_OWNER can create
     */
    @PostMapping("/api/restaurants/{restaurantId}/menu-items")
    public ResponseEntity<MenuItemResponse> createMenuItem(
            @PathVariable Long restaurantId,
            @RequestBody @Valid CreateMenuItemRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        // AUTHORIZATION CHECK AT CONTROLLER LEVEL
        if (!user.getRole().contains(UserRole.RESTAURANT_OWNER)) {
            throw new UnauthorizedException("Only restaurant owners can add menu items");
        }

        // Verify ownership using RestaurantService helper
        restaurantService.verifyRestaurantOwnership(restaurantId, user.getId());

        MenuItemResponse response = menuItemService.createMenuItem(restaurantId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/restaurants/{restaurantId}/menu-items/{menuItemId}
     * Get a specific menu item by ID
     */
    @GetMapping("/api/restaurants/{restaurantId}/menu-items/{menuItemId}")
    public ResponseEntity<MenuItemResponse> getMenuItemById(
            @PathVariable Long restaurantId,
            @PathVariable Long menuItemId) {

        MenuItemResponse response = menuItemService.getMenuItemById(menuItemId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/restaurants/{restaurantId}/menu-items
     * Get all menu items for a restaurant with pagination
     */
    @GetMapping("/api/restaurants/{restaurantId}/menu-items")
    public ResponseEntity<List<MenuItemResponse>> getMenuItemsByRestaurant(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<MenuItemResponse> response = menuItemService.getMenuItemsByRestaurant(restaurantId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/menu-items
     * Get all menu items by name
     */
    @GetMapping("/api/menu-items")
    public ResponseEntity<List<MenuItemResponse>> getMenuItemByName(@RequestParam String name) {
        List<MenuItemResponse> responses = menuItemService.getMenuItemByName(name);
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/restaurants/{restaurantId}/menu-items/category/{category}
     * Get menu items by category for a restaurant
     */
    @GetMapping("/api/restaurants/{restaurantId}/menu-items/category")
    public ResponseEntity<List<MenuItemResponse>> getMenuItemsByCategory(
            @PathVariable Long restaurantId,
            @RequestParam String category) {

        List<MenuItemResponse> response = menuItemService.getMenuItemsByRestaurantAndCategory(restaurantId, category);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/restaurants/{restaurantId}/menu-items/available
     * Get only available menu items for a restaurant
     */
    @GetMapping("/api/restaurants/{restaurantId}/menu-items/available")
    public ResponseEntity<List<MenuItemResponse>> getAvailableMenuItems(
            @PathVariable Long restaurantId) {

        List<MenuItemResponse> response = menuItemService.getAvailableMenuItems(restaurantId);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/restaurants/{restaurantId}/menu-items/{menuItemId}
     * Update a menu item (PATCH semantics)
     * Authorization: Only the restaurant owner can update
     */
    @PutMapping("/api/restaurants/{restaurantId}/menu-items/{menuItemId}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long menuItemId,
            @RequestBody Map<String, Object> updates,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        // Verify ownership using RestaurantService helper
        restaurantService.verifyRestaurantOwnership(restaurantId, user.getId());

        // Verify menu item belongs to this restaurant
        MenuItemResponse menuItem = menuItemService.getMenuItemById(menuItemId);
        if (!menuItem.getRestaurant().getId().equals(restaurantId)) {
            throw new ResourceNotFoundException("Menu item not found in this restaurant");
        }

        MenuItemResponse response = menuItemService.updateMenuItem(menuItemId, updates);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/restaurants/{restaurantId}/menu-items/{menuItemId}
     * Delete a menu item
     * Authorization: Only the restaurant owner can delete
     */
    @DeleteMapping("/api/restaurants/{restaurantId}/menu-items/{menuItemId}")
    public ResponseEntity<DeleteResponse> deleteMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long menuItemId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        // Verify ownership using RestaurantService helper
        restaurantService.verifyRestaurantOwnership(restaurantId, user.getId());

        // Verify menu item belongs to this restaurant
        MenuItemResponse menuItem = menuItemService.getMenuItemById(menuItemId);
        if (!menuItem.getRestaurant().getId().equals(restaurantId)) {
            throw new ResourceNotFoundException("Menu item not found in this restaurant");
        }

        DeleteResponse deleteResponse = menuItemService.deleteMenuItem(menuItemId);
        return ResponseEntity.ok(deleteResponse);
    }

    /**
     * PATCH /api/restaurants/{restaurantId}/menu-items/{menuItemId}/toggle-availability
     * Toggle availability of a menu item
     * Authorization: Only the restaurant owner can toggle
     */
    @PatchMapping("/api/restaurants/{restaurantId}/menu-items/{menuItemId}/toggle-availability")
    public ResponseEntity<MenuItemResponse> toggleAvailability(
            @PathVariable Long restaurantId,
            @PathVariable Long menuItemId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        // Verify ownership using RestaurantService helper
        restaurantService.verifyRestaurantOwnership(restaurantId, user.getId());

        // Verify menu item belongs to this restaurant
        MenuItemResponse menuItem = menuItemService.getMenuItemById(menuItemId);
        if (!menuItem.getRestaurant().getId().equals(restaurantId)) {
            throw new ResourceNotFoundException("Menu item not found in this restaurant");
        }

        MenuItemResponse response = menuItemService.toggleAvailability(menuItemId);
        return ResponseEntity.ok(response);
    }
}