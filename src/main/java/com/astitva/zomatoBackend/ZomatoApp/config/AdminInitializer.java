package com.astitva.zomatoBackend.ZomatoApp.config;

import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.UserRole;
import com.astitva.zomatoBackend.ZomatoApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // if admin already exists, skip
        boolean adminExists = userRepository.existsByRoleContaining(UserRole.ADMIN);

        if (!adminExists) {
            User admin = new User();
            admin.setName("System Administrator");
            admin.setEmail("admin@zomatoapp.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(Set.of(UserRole.ADMIN));

            userRepository.save(admin);

            System.out.println("🚀 ADMIN USER CREATED → email: admin@zomatoapp.com , pass: Admin@123");
        }
    }
}
