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
import org.apache.el.util.ReflectionUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private Address findAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address Not found"));
    }

    @Override
    public AddressResponse addAddress(Long userId, Long requesterId, CreateAddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Address address = modelMapper.map(request, Address.class);
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressResponse.class);
    }

    @Override
    public List<AddressResponse> getAddressesByUser(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);

        return addresses
                .stream()
                .map(address -> modelMapper.map(address, AddressResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponse updateAddress(Long addressId, Map<String, Object> request, Long requesterUserId) {
        Address address = findAddressById(addressId);

        request.forEach((field, value) -> {
            Field field1 = ReflectionUtils.findField(Address.class, field);
            field1.setAccessible(true);
            ReflectionUtils.setField(field1, address, value);
        });

        Address updatedAddress = addressRepository.save(address);
        return modelMapper.map(updatedAddress, AddressResponse.class);
    }

    @Override
    public boolean deleteAddress(Long addressId, Long requesterUserId) {
        Address address = findAddressById(addressId);
        addressRepository.delete(address);
        return true;
    }
}
