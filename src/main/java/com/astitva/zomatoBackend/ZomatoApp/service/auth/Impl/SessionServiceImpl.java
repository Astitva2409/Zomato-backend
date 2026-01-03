package com.astitva.zomatoBackend.ZomatoApp.service.auth.Impl;

import com.astitva.zomatoBackend.ZomatoApp.entities.Session;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.repository.SessionRepository;
import com.astitva.zomatoBackend.ZomatoApp.service.auth.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final int SESSION_LIMIT = 2;

    @Override
    public void generateNewSession(User user, String refreshToken, String accessToken) {
        List<Session> userSessions = sessionRepository.findByUser(user);
        if (userSessions.size() == SESSION_LIMIT) {
            userSessions.sort(Comparator.comparing(session -> session.getLastUsedAt()));
            sessionRepository.delete(userSessions.getFirst());
        }

        Session newSession = Session.builder()
                .user(user)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        sessionRepository.save(newSession);
    }

    @Override
    public Session validateSession(String refreshToken) {
        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new SessionAuthenticationException("Session not found"));

        session.setLastUsedAt(LocalDateTime.now());
        return sessionRepository.save(session);
    }

    @Override
    public void deleteSession(String refreshToken) {
        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new SessionAuthenticationException("Session not found"));
        sessionRepository.delete(session);
    }

    @Override
    public boolean isAccessTokenValidForUser(Long userId, String accessToken) {
        return sessionRepository.findByUserIdAndAccessToken(userId, accessToken).isPresent();
    }
}