package com.astitva.zomatoBackend.ZomatoApp.service.restaurant.Impl;

import com.astitva.zomatoBackend.ZomatoApp.dto.*;
import com.astitva.zomatoBackend.ZomatoApp.entities.Restaurant;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.exception.ResourceNotFoundException;
import com.astitva.zomatoBackend.ZomatoApp.repository.RestaurantRepository;
import com.astitva.zomatoBackend.ZomatoApp.repository.UserRepository;
import com.astitva.zomatoBackend.ZomatoApp.service.restaurant.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * Helper method to build UserResponse with addresses
     * Manually maps addresses since ModelMapper is configured to skip them
     */
    private UserResponse buildUserResponse(User owner) {
        // Map user to UserResponse (addresses will be skipped by ModelMapper config)
        UserResponse userResponse = modelMapper.map(owner, UserResponse.class);

        // Manually set addresses by mapping them
        if (owner.getAddresses() != null && !owner.getAddresses().isEmpty()) {
            List<AddressResponse> addressResponses = owner.getAddresses()
                    .stream()
                    .map(address -> modelMapper.map(address, AddressResponse.class))
                    .toList();
            userResponse.setAddresses(addressResponses);
        }

        return userResponse;
    }

    /**
     * Helper method to convert Restaurant to RestaurantResponse
     * Handles complete mapping including owner with addresses
     */
    private RestaurantResponse convertToResponse(Restaurant restaurant) {
        // Map restaurant basic fields
        RestaurantResponse response = modelMapper.map(restaurant, RestaurantResponse.class);

        // Manually map owner with addresses
        if (restaurant.getOwner() != null) {
            User owner = restaurant.getOwner();
            response.setOwner(buildUserResponse(owner));  // Use helper to build owner with addresses
        }

        return response;
    }

    @Override
    @Transactional
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request, Long ownerId) {
        // Fetch the owner
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Map request to entity
        Restaurant restaurant = modelMapper.map(request, Restaurant.class);
        restaurant.setOwner(owner);

        // Save to database
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        // Return response using helper method
        return convertToResponse(savedRestaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurantById(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("restaurant not found with provided id"));

        // Use helper method to convert
        return convertToResponse(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getAllRestaurants(Pageable pageable) {
        // ✅ FIXED: Use convertToResponse helper for each restaurant
        return restaurantRepository.findAll(pageable)
                .map(this::convertToResponse)
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RestaurantResponse> getRestaurantsByCity(String city, Pageable pageable) {
        return null;
    }

    @Override
    @Transactional
    public RestaurantResponse updateRestaurant(Long restaurantId, Map<String, Object> updates) {
        // Find restaurant
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        updates.forEach((field, value) -> {
            Field field1 = ReflectionUtils.findField(Restaurant.class, field);
            field1.setAccessible(true);
            ReflectionUtils.setField(field1, restaurant, value);
        });

        // Save
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);

        // Use helper method
        return convertToResponse(updatedRestaurant);
    }

    @Override
    @Transactional
    public DeleteRestaurantResponse deleteRestaurant(Long restaurantId) {
        // Find restaurant
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        // Delete (cascade will handle menu items)
        restaurantRepository.delete(restaurant);

        // Return success message
        return DeleteRestaurantResponse.builder()
                .message("Restaurant deleted successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getRestaurantsByOwner(Long ownerId) {
        List<Restaurant> restaurants = restaurantRepository.findByOwnerId(ownerId);
        if(restaurants.isEmpty()) {
            throw new ResourceNotFoundException("This owner has no restaurants");
        }
        return restaurants.stream()
                .map(this::convertToResponse)
                .toList();
    }
}
