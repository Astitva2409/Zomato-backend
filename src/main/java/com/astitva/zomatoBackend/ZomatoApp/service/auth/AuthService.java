package com.astitva.zomatoBackend.ZomatoApp.service.auth;

import com.astitva.zomatoBackend.ZomatoApp.dto.AuthRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.AuthResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.RegisterUserRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.UserResponse;

public interface AuthService {
    UserResponse register(RegisterUserRequest request);
    AuthResponse login(AuthRequest request);
}
