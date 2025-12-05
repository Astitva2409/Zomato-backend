package com.astitva.zomatoBackend.ZomatoApp.controller;

import com.astitva.zomatoBackend.ZomatoApp.dto.LogoutResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.UserResponse;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId,
                                            Authentication authentication) {
        User requester = (User) authentication.getPrincipal();
        UserResponse userResponse = userService.getUserById(userId, requester.getId());

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/admin/all-users")
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size,
                                                          Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        List<UserResponse> response = userService.getAllUsers(pageable, admin.getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<LogoutResponse> deleteUser(@PathVariable Long userId,
                                                     Authentication authentication) {
        User admkin = (User) authentication.getPrincipal();
        LogoutResponse response = userService.deleteUser(userId, admkin.getId());
        return ResponseEntity.ok(response);
    }

//    @PutMapping("/admin/update/{userId}")
//    public ResponseEntity<LogoutResponse> updateUserRole()


}
