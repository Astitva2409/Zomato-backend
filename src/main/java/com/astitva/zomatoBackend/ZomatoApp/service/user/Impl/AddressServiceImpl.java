package com.astitva.zomatoBackend.ZomatoApp.service.user.Impl;

import com.astitva.zomatoBackend.ZomatoApp.dto.AddressResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.CreateAddressRequest;
import com.astitva.zomatoBackend.ZomatoApp.entities.Address;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.exception.ResourceNotFoundException;
import com.astitva.zomatoBackend.ZomatoApp.exception.UnauthorizedException;
import com.astitva.zomatoBackend.ZomatoApp.repository.AddressRepository;
import com.astitva.zomatoBackend.ZomatoApp.repository.UserRepository;
import com.astitva.zomatoBackend.ZomatoApp.service.user.AddressService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /** Only the requester can manage their own addresses */
    private void validateUser(Long userId, Long requesterId) {
        if (!userId.equals(requesterId)) {
            throw new UnauthorizedException("You cannot manage another user's addresses");
        }
    }

    @Override
    public AddressResponse addAddress(Long userId, Long requesterId, CreateAddressRequest request) {
        validateUser(userId, requesterId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Address address = modelMapper.map(request, Address.class);
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressResponse.class);
    }

    @Override
    public List<AddressResponse> getAddressesByUser(Long userId) {
        return List.of();
    }

    @Override
    public AddressResponse updateAddress(Long addressId, CreateAddressRequest request, Long requesterUserId) {
        return null;
    }

    @Override
    public void deleteAddress(Long addressId, Long requesterUserId) {

    }
}
