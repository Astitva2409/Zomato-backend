package com.astitva.zomatoBackend.ZomatoApp.repository;

import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByRole(UserRole role, Pageable pageable);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
