package com.astitva.zomatoBackend.ZomatoApp.service.auth;

import com.astitva.zomatoBackend.ZomatoApp.entities.User;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    Long getUserIdFromToken(String token);

    boolean isTokenValid(String token);
}
