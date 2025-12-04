package com.astitva.zomatoBackend.ZomatoApp.service.user.Impl;

import com.astitva.zomatoBackend.ZomatoApp.dto.RegisterUserRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.UserResponse;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.UserRole;
import com.astitva.zomatoBackend.ZomatoApp.exception.ResourceNotFoundException;
import com.astitva.zomatoBackend.ZomatoApp.exception.UnauthorizedException;
import com.astitva.zomatoBackend.ZomatoApp.repository.UserRepository;
import com.astitva.zomatoBackend.ZomatoApp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(Long userId, Long requesterUserId) {
        User requester = userRepository.findById(requesterUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Requester user not found"));

        if(!requesterUserId.equals(userId) && !requester.getRole().contains(UserRole.ADMIN)) {
            throw new UnauthorizedException("You are not allowed to view this user's details");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable, Long adminUserId) {
        return null;
    }

    @Override
    public void deleteUser(Long userId, Long adminUserId) {

    }

    @Override
    public void updateUserRole(Long userId, UserRole newRole, Long adminUserId) {

    }

    @Override
    public Page<UserResponse> getUsersByRole(UserRole role, Pageable pageable, Long adminUserId) {
        return null;
    }
}
