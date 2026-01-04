package com.astitva.zomatoBackend.ZomatoApp.service.menuitem;

import com.astitva.zomatoBackend.ZomatoApp.dto.DeleteResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.MenuItemResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.CreateMenuItemRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface MenuItemService {

    /**
     * Create a new menu item for a restaurant
     * Authorization check: Done in controller
     */
    MenuItemResponse createMenuItem(Long restaurantId, CreateMenuItemRequest request);

    /**
     * Get a menu item by ID
     */
    MenuItemResponse getMenuItemById(Long menuItemId);

    /**
     * Get all menu items for a specific restaurant with pagination
     */
    List<MenuItemResponse> getMenuItemsByRestaurant(Long restaurantId, Pageable pageable);

    /**
     * Get all menu items for a restaurant (non-paginated)
     */
    List<MenuItemResponse> getMenuItemByName(String name);

    /**
     * Get menu items by category for a restaurant
     */
    List<MenuItemResponse> getMenuItemsByRestaurantAndCategory(Long restaurantId, String category);

    /**
     * Get available menu items for a restaurant
     */
    List<MenuItemResponse> getAvailableMenuItems(Long restaurantId);

    /**
     * Update a menu item with dynamic fields
     * Protected fields (id, createdAt, updatedAt, restaurant) cannot be updated
     * Authorization check: Done in controller
     */
    MenuItemResponse updateMenuItem(Long menuItemId, Map<String, Object> updates);

    /**
     * Delete a menu item
     * Authorization check: Done in controller
     */
    DeleteResponse deleteMenuItem(Long menuItemId);

    /**
     * Toggle availability of a menu item
     * Authorization check: Done in controller
     */
    MenuItemResponse toggleAvailability(Long menuItemId);
}
