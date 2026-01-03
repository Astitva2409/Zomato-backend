package com.astitva.zomatoBackend.ZomatoApp.filters;

import com.astitva.zomatoBackend.ZomatoApp.dto.UserResponse;
import com.astitva.zomatoBackend.ZomatoApp.entities.User;
import com.astitva.zomatoBackend.ZomatoApp.service.auth.JwtService;
import com.astitva.zomatoBackend.ZomatoApp.service.auth.SessionService;
import com.astitva.zomatoBackend.ZomatoApp.service.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final SessionService sessionService;
    private final ModelMapper modelMapper;

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String requestHeaderToken = request.getHeader("Authorization");
            if (requestHeaderToken == null || !requestHeaderToken.startsWith("Bearer")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = requestHeaderToken.split("Bearer ")[1];
            Long userId = jwtService.getUserIdFromToken(token);

            if (!sessionService.isAccessTokenValidForUser(userId, token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid session or logged out\"}");
                return;
            }

            if (!jwtService.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (userId != null) {
                UserResponse userResponse = userService.getUserById(userId);
                User user = modelMapper.map(userResponse, User.class);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
