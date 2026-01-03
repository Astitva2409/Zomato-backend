package com.astitva.zomatoBackend.ZomatoApp.service.user.Impl;

import com.astitva.zomatoBackend.ZomatoApp.dto.AddressResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.LogoutResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.UserResponse;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.UserRole;
import com.astitva.zomatoBackend.ZomatoApp.exception.ResourceNotFoundException;
import com.astitva.zomatoBackend.ZomatoApp.exception.UnauthorizedException;
import com.astitva.zomatoBackend.ZomatoApp.repository.SessionRepository;
import com.astitva.zomatoBackend.ZomatoApp.repository.UserRepository;
import com.astitva.zomatoBackend.ZomatoApp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final SessionRepository sessionRepository;

    private void validateAdmin(Long adminUserId) {
        User user = loadUserEntity(adminUserId);
        if(!user.getRole().contains(UserRole.ADMIN)) {
            throw new UnauthorizedException("Only admin can perform this action");
        }
    }

    @Override
    public User loadUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = loadUserEntity(userId);

        UserResponse response = modelMapper.map(user, UserResponse.class);

        List<AddressResponse> addressResponses = user.getAddresses()
                .stream()
                .map(address -> modelMapper.map(address, AddressResponse.class))
                .toList();

        response.setAddresses(addressResponses);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(Pageable pageable) {
        List<User> usersPage = userRepository.findAll(pageable).getContent();

        return usersPage.stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .toList();

//        return usersPage.map(user -> {
//            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
//            List<Address> addressResponseList = user.getAddresses()
//                    .stream()
//                    .map(address -> modelMapper.map(address, Address.class))
//                    .toList();
//
//            userResponse.setAddresses(addressResponseList);
//            return userResponse;
//        });
    }

    @Override
    public LogoutResponse deleteUser(Long userId) {
        User user = loadUserEntity(userId);
        sessionRepository.deleteAll(sessionRepository.findByUser(user));
        userRepository.delete(user);

        return new LogoutResponse("User deleted successfully.");
    }

    @Override
    public UserResponse updateUserRole(Long userId, UserRole newRole) {
        User user = loadUserEntity(userId);
        user.getRole().add(newRole);
        userRepository.save(user);

        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public Page<UserResponse> getUsersByRole(UserRole role, Pageable pageable, Long adminUserId) {
        return null;
    }
}
