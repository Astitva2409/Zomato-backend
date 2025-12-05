package com.astitva.zomatoBackend.ZomatoApp.service.user;

import com.astitva.zomatoBackend.ZomatoApp.dto.LogoutResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.RegisterUserRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.UserResponse;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    User loadUserEntity(Long userId);

    // A user can view their own profile; admin can view any user
    UserResponse getUserById(Long userId, Long requesterUserId);

    // admin only
    List<UserResponse> getAllUsers(Pageable pageable, Long adminUserId);

    // admin only
    LogoutResponse deleteUser(Long userId, Long adminUserId);

    // ADMIN only
    void updateUserRole(Long userId, UserRole newRole, Long adminUserId);

    // ADMIN only
    Page<UserResponse> getUsersByRole(UserRole role, Pageable pageable, Long adminUserId);

}