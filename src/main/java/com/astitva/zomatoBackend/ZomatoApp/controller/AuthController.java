package com.astitva.zomatoBackend.ZomatoApp.controller;

import com.astitva.zomatoBackend.ZomatoApp.dto.AuthRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.AuthResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.RegisterUserRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.UserResponse;
import com.astitva.zomatoBackend.ZomatoApp.service.auth.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterUserRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest loginRequest, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(loginRequest);
        Cookie cookie = new Cookie("refreshToken", authResponse.getRefreshToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok(authResponse);
    }
}
