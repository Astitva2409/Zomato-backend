package com.astitva.zomatoBackend.ZomatoApp.controller;

import com.astitva.zomatoBackend.ZomatoApp.dto.AddressResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.CreateAddressRequest;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.exception.UnauthorizedException;
import com.astitva.zomatoBackend.ZomatoApp.service.user.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/users")
public class AddressController {

    private final AddressService addressService;

    /**
     * POST /api/users/{userId}/address
     * Add a new address for a user
     */
    @PostMapping("/{userId}/create-address")
    public ResponseEntity<AddressResponse> addAddress(@PathVariable Long userId
            ,@RequestBody @Valid CreateAddressRequest addressRequest
            ,Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        // Validate user can only view their own addresses
        if (!user.getId().equals(userId)) {
            throw new UnauthorizedException("You can only view your own addresses");
        }

        AddressResponse addressResponse = addressService.addAddress(userId, user.getId(), addressRequest);
        return ResponseEntity.ok(addressResponse);
    }

    /**
     * GET /api/users/{userId}/address
     * Get all addresses for a user
     */
    @GetMapping("/{userId}/address")
    public ResponseEntity<List<AddressResponse>> getAddressesByUser(@PathVariable Long userId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        // Validate user can only view their own addresses
        if (!user.getId().equals(userId)) {
            throw new UnauthorizedException("You can only view your own addresses");
        }

        List<AddressResponse> addresses = addressService.getAddressesByUser(userId);
        return ResponseEntity.ok(addresses);
    }

    /**
     * PUT /api/users/{userId}/address/{addressId}
     * Update a user's address
     */
    @PutMapping("/{userId}/address/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @RequestBody Map<String, Object> addressRequest,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        // Double check: validate the address belongs to the user in path
        if (!user.getId().equals(userId)) {
            throw new UnauthorizedException("You can only update your own addresses");
        }

        AddressResponse updatedAddress = addressService.updateAddress(addressId, addressRequest, user.getId());
        return ResponseEntity.ok(updatedAddress);
    }

    /**
     * DELETE /api/users/{userId}/address/{addressId}
     * Delete a user's address
     */
    @DeleteMapping("/{userId}/address/{addressId}")
    public boolean deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        if (!user.getId().equals(userId)) {
            throw new UnauthorizedException("You can only delete your own addresses");
        }

        addressService.deleteAddress(addressId, user.getId());
        return true;
    }
}