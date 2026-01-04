package com.astitva.zomatoBackend.ZomatoApp.service.menuitem.Impl;

import com.astitva.zomatoBackend.ZomatoApp.dto.CreateMenuItemRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.DeleteResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.MenuItemResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.RestaurantBasicDTO;
import com.astitva.zomatoBackend.ZomatoApp.entities.MenuItem;
import com.astitva.zomatoBackend.ZomatoApp.entities.Restaurant;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.MenuCategory;
import com.astitva.zomatoBackend.ZomatoApp.exception.ResourceNotFoundException;
import com.astitva.zomatoBackend.ZomatoApp.repository.MenuItemRepository;
import com.astitva.zomatoBackend.ZomatoApp.service.menuitem.MenuItemService;
import com.astitva.zomatoBackend.ZomatoApp.service.restaurant.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final ModelMapper modelMapper;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantService restaurantService;

    // Fields that should NOT be updated
    private static final Set<String> PROTECTED_FIELDS = Set.of(
            "id", "createdAt", "updatedAt", "restaurant"
    );

    /**
     * Helper method: Build RestaurantBasicDTO from Restaurant
     * Uses ModelMapper for clean mapping
     */
    private RestaurantBasicDTO buildRestaurantBasicDTO(Restaurant restaurant) {
        return modelMapper.map(restaurant, RestaurantBasicDTO.class);
    }

    /**
     * Helper method: Convert MenuItem to MenuItemResponse
     */
    private MenuItemResponse convertToResponse(MenuItem menuItem) {
        MenuItemResponse response = modelMapper.map(menuItem, MenuItemResponse.class);

        if (menuItem.getRestaurant() != null) {
            Restaurant restaurant = menuItem.getRestaurant();
            response.setRestaurant(buildRestaurantBasicDTO(restaurant));
        }

        return response;
    }

    /**
     * Helper method: Safely update entity fields using reflection
     * Prevents updating protected fields
     */
    private void updateEntityFields(MenuItem menuItem, Map<String, Object> updates) {
        updates.forEach((fieldName, value) -> {
            // Prevent updating protected fields
            if (PROTECTED_FIELDS.contains(fieldName)) {
                return;  // Skip this field
            }

            Field field = ReflectionUtils.findField(MenuItem.class, fieldName);
            if (field != null) {
                field.setAccessible(true);
                try {
                    ReflectionUtils.setField(field, menuItem, value);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid value for field: " + fieldName);
                }
            }
        });
    }

    @Override
    @Transactional
    public MenuItemResponse createMenuItem(Long restaurantId, CreateMenuItemRequest request) {
        // Use helper from RestaurantService to get restaurant
        Restaurant restaurant = restaurantService.getRestaurantOrThrow(restaurantId);

        // Map the menu item request payload with the menu item entity
        MenuItem menuItem = modelMapper.map(request, MenuItem.class);

        // set the restaurant inside the menu item separately
        menuItem.setRestaurant(restaurant);

        // save the menu item to the DB
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);

        // return the response
        return convertToResponse(savedMenuItem);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemResponse getMenuItemById(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        return convertToResponse(menuItem);

    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByRestaurant(Long restaurantId, Pageable pageable) {
        // Verify restaurant exists using helper
        restaurantService.getRestaurantOrThrow(restaurantId);

        return menuItemRepository.findByRestaurantId(restaurantId, pageable)
                .map(this::convertToResponse)
                .getContent();
    }

    @Override
    public List<MenuItemResponse> getMenuItemByName(String name) {
        List<MenuItem> menuItemResponses = menuItemRepository.findByNameIgnoreCase(name);

        if (menuItemResponses.isEmpty()) {
            throw new ResourceNotFoundException("No items found with provided name");
        }

        return menuItemResponses
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByRestaurantAndCategory(Long restaurantId, String category) {
        // Verify restaurant exists using helper
        restaurantService.getRestaurantOrThrow(restaurantId);

        return menuItemRepository.findByRestaurantIdAndCategoryIgnoreCase(restaurantId, category)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getAvailableMenuItems(Long restaurantId) {
        // Verify restaurant exists using helper
        restaurantService.getRestaurantOrThrow(restaurantId);

        return menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional
    public MenuItemResponse updateMenuItem(Long menuItemId, Map<String, Object> updates) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        // Safe field update with reflection
        updateEntityFields(menuItem, updates);

        // set the updated At field
        menuItem.setUpdatedAt(LocalDateTime.now());

        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);
        return convertToResponse(updatedMenuItem);
    }

    @Override
    @Transactional
    public DeleteResponse deleteMenuItem(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        menuItemRepository.delete(menuItem);
        DeleteResponse deleteResponse = DeleteResponse
                .builder()
                .message("Menu item deleted successfully with id "+menuItemId)
                .build();

        return deleteResponse;
    }

    @Override
    @Transactional
    public MenuItemResponse toggleAvailability(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        menuItem.setAvailable(!menuItem.isAvailable());
        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);

        return convertToResponse(updatedMenuItem);
    }
}