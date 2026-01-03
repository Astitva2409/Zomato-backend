package com.astitva.zomatoBackend.ZomatoApp.service.user;

import com.astitva.zomatoBackend.ZomatoApp.dto.AddressResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.CreateAddressRequest;

import java.util.List;
import java.util.Map;

public interface AddressService {
    AddressResponse addAddress(Long userId, Long requesterId, CreateAddressRequest request);

    List<AddressResponse> getAddressesByUser(Long userId);

    // Only the owner of the address should be able to update it
    AddressResponse updateAddress(Long addressId, Map<String, Object> request, Long requesterUserId);

    // Only the owner of the address should be able to delete it
    boolean deleteAddress(Long addressId, Long requesterUserId);
}
