package com.astitva.zomatoBackend.ZomatoApp.controller;

import com.astitva.zomatoBackend.ZomatoApp.dto.LogoutResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.UpdateUserRoleRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.UserResponse;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.UserRole;
import com.astitva.zomatoBackend.ZomatoApp.exception.UnauthorizedException;
import com.astitva.zomatoBackend.ZomatoApp.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(path = "/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId,
                                            Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        // Validate if requesting user is same as signedIn user
        if(!user.getId().equals(userId)) {
            throw new UnauthorizedException("Not allowed to view another user's details");
        }

        UserResponse userResponse = userService.getUserById(userId);

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/admin/all-users")
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size,
                                                          Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if(!user.getRole().contains(UserRole.ADMIN)) {
            throw new UnauthorizedException("Only admin can perform this action");
        }

        Pageable pageable = PageRequest.of(page, size);
        List<UserResponse> response = userService.getAllUsers(pageable);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<LogoutResponse> deleteUser(@PathVariable Long userId,
                                                     Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if(!user.getRole().contains(UserRole.ADMIN)) {
            throw new UnauthorizedException("Only admin can perform this action");
        }

        if (userId == 1) {
            throw new UnauthorizedException("Admin cannot be deleted");
        }

        LogoutResponse response = userService.deleteUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/users/admin/update/{userId}
     * Update user's role
     * Only ADMIN can update user roles
     * Payload: {"role": "RESTAURANT_OWNER"}
     * Authorization: Checked at controller level
     */
    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<UserResponse> updateUserByRole(
            @PathVariable Long userId,
            @RequestBody @Valid UpdateUserRoleRequest request,
            Authentication authentication) {

        User admin = (User) authentication.getPrincipal();

        // Authorization check at controller
        if (!admin.getRole().contains(UserRole.ADMIN)) {
            throw new UnauthorizedException("Only admins can update user roles");
        }

        // Prevent creating new ADMIN (security check)
        if (request.getRole() == UserRole.ADMIN) {
            throw new UnauthorizedException("Creating a new admin is not allowed.");
        }

        // Prevent self-role-change
        if (Objects.equals(userId, admin.getId())) {
            throw new UnauthorizedException("Admin cannot change their own role");
        }

        // Call service to update role
        UserResponse userResponse = userService.updateUserRole(userId, request.getRole());

        // Return success response
        return ResponseEntity.ok(userResponse);
    }


}
