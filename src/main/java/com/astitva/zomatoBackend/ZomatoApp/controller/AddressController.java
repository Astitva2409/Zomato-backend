package com.astitva.zomatoBackend.ZomatoApp.controller;

import com.astitva.zomatoBackend.ZomatoApp.dto.AddressResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.CreateAddressRequest;
import com.astitva.zomatoBackend.ZomatoApp.entities.Address;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.service.user.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/users")
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/{userId}/address")
    public ResponseEntity<AddressResponse> addAddress(@PathVariable Long userId
            ,@RequestBody @Valid CreateAddressRequest addressRequest
            ,Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        AddressResponse addressResponse = addressService.addAddress(userId, user.getId(), addressRequest);
        return ResponseEntity.ok(addressResponse);
    }
}
