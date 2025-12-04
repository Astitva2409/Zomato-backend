package com.astitva.zomatoBackend.ZomatoApp.service.auth;

import com.astitva.zomatoBackend.ZomatoApp.entities.User;

public interface SessionService {
    void generateNewSession(User user, String refreshToken, String accessToken);
    void validateSession(String refreshToken);
    void deleteSession(String refreshToken);
    boolean isAccessTokenValidForUser(Long userId, String accessToken);
}
