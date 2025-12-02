package com.astitva.zomatoBackend.ZomatoApp.service.user;

import com.astitva.zomatoBackend.ZomatoApp.dto.RegisterUserRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.UserResponse;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse registerUser(RegisterUserRequest request);

    // A user can view their own profile; admin can view any user
    UserResponse getUserById(Long userId, Long requesterUserId);

    // admin only
    Page<UserResponse> getAllUsers(Pageable pageable, Long adminUserId);

    // admin only
    void deleteUser(Long userId, Long adminUserId);

    // ADMIN only
    void updateUserRole(Long userId, UserRole newRole, Long adminUserId);

    // ADMIN only
    Page<UserResponse> getUsersByRole(UserRole role, Pageable pageable, Long adminUserId);
}
