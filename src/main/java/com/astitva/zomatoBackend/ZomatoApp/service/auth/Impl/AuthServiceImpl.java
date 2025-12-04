package com.astitva.zomatoBackend.ZomatoApp.service.auth.Impl;

import com.astitva.zomatoBackend.ZomatoApp.dto.AuthRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.AuthResponse;
import com.astitva.zomatoBackend.ZomatoApp.dto.RegisterUserRequest;
import com.astitva.zomatoBackend.ZomatoApp.dto.UserResponse;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.UserRole;
import com.astitva.zomatoBackend.ZomatoApp.exception.RuntimeConflictException;
import com.astitva.zomatoBackend.ZomatoApp.exception.UnauthorizedException;
import com.astitva.zomatoBackend.ZomatoApp.repository.UserRepository;
import com.astitva.zomatoBackend.ZomatoApp.service.auth.AuthService;
import com.astitva.zomatoBackend.ZomatoApp.service.auth.JwtService;
import com.astitva.zomatoBackend.ZomatoApp.service.auth.SessionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SessionService sessionService;

    @Override
    public UserResponse register(RegisterUserRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user != null)
            throw new RuntimeConflictException("Cannot signup. User already exists");

        User mappedUser = modelMapper.map(request, User.class);
        mappedUser.setRole(Set.of(UserRole.CUSTOMER));
        mappedUser.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(mappedUser);

        return modelMapper.map(savedUser, UserResponse.class);
    }

    @Override
    public AuthResponse login(AuthRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            sessionService.generateNewSession(user, refreshToken, accessToken);
            return new AuthResponse(user.getId(), accessToken, refreshToken);

        } catch (Exception e) {
            throw new UnauthorizedException("Invalid email or password");
        }
    }
}
