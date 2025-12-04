package com.astitva.zomatoBackend.ZomatoApp.repository;

import com.astitva.zomatoBackend.ZomatoApp.entities.Session;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByUser(User user);

    Optional<Session> findByRefreshToken(String refreshToken);

    Optional<Session> findByUserIdAndAccessToken(Long userId, String accessToken);

}
