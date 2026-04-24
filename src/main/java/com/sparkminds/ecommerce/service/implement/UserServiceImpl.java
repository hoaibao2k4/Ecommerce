package com.sparkminds.ecommerce.service.implement;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.sparkminds.ecommerce.dto.response.AuthenticationResponse;
import com.sparkminds.ecommerce.entity.User;
import com.sparkminds.ecommerce.repository.UserRepository;
import com.sparkminds.ecommerce.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getCurrentUser() {
        // SecurityContext
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String keycloakId;
        if (principal instanceof Jwt jwt) {
            keycloakId = jwt.getSubject();
        } else {
            keycloakId = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        return userRepository.findByKeycloakId(keycloakId).orElseThrow(
                () -> new AccessDeniedException("Access denied: User not found in database with ID: " + keycloakId));
    }

    @Override
    public AuthenticationResponse getUserInfo() {
        User user = getCurrentUser();
        return AuthenticationResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

}