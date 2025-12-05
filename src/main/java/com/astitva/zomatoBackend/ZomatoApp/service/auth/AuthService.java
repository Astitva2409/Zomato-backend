package com.astitva.zomatoBackend.ZomatoApp.service.auth;

import com.astitva.zomatoBackend.ZomatoApp.dto.*;

public interface AuthService {
    UserResponse register(RegisterUserRequest request);
    AuthResponse login(AuthRequest request);
    AuthResponse refresh(String refreshToken);
    LogoutResponse logout(String refreshToken);
}
