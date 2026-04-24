package com.sparkminds.ecommerce.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

import com.sparkminds.ecommerce.entity.User;
import com.sparkminds.ecommerce.enumerator.Role;
import com.sparkminds.ecommerce.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRepository userRepository;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Create converter and get username
        JwtAuthenticationConverter defaultConverter = new JwtAuthenticationConverter();
        defaultConverter.setPrincipalClaimName("preferred_username");

        // extract role
        List<GrantedAuthority> authorities = extractAuthorities(jwt);
        defaultConverter.setJwtGrantedAuthoritiesConverter(token -> authorities);

        // JIT Sync
        syncUserToDatabase(jwt, authorities);

        return defaultConverter.convert(jwt);
    }

    private List<GrantedAuthority> extractAuthorities(Jwt jwt) {
        List<String> allRoles = new ArrayList<>();

        // Client Role
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("client-springboot");
            if (clientAccess != null && clientAccess.get("roles") instanceof Collection<?> roles) {
                roles.forEach(r -> allRoles.add(r.toString()));
            }
        }

        return allRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }

    private void syncUserToDatabase(Jwt jwt, List<GrantedAuthority> authorities) {
        String keycloakId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");

        // Check role
        final Role tokenRole = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))
                        ? Role.ADMIN
                        : Role.USER;

        // Sync user
        userRepository.findByKeycloakId(keycloakId).ifPresentOrElse(
                existingUser -> {
                    // If user exists: Check and update if there are changes
                    boolean isUpdated = false;
                    if (!existingUser.getRole().equals(tokenRole)) {
                        existingUser.setRole(tokenRole);
                        isUpdated = true;
                    }
                    if (email != null && !email.equals(existingUser.getEmail())) {
                        existingUser.setEmail(email);
                        isUpdated = true;
                    }
                    if (username != null && !username.equals(existingUser.getUsername())) {
                        existingUser.setUsername(username);
                        isUpdated = true;
                    }

                    if (isUpdated) {
                        userRepository.save(existingUser);
                    }
                },
                () -> {
                    // If user not found by keycloakId, try to find by username
                    userRepository.findByUsername(username).ifPresentOrElse(
                            existingUserByUsername -> {
                                existingUserByUsername.setKeycloakId(keycloakId);
                                existingUserByUsername.setEmail(email);
                                existingUserByUsername.setRole(tokenRole);
                                userRepository.save(existingUserByUsername);
                            },
                            () -> {
                                User newUser = User.builder()
                                        .keycloakId(keycloakId)
                                        .username(username)
                                        .email(email)
                                        .role(tokenRole)
                                        .build();
                                userRepository.save(newUser);
                            }
                    );
                });
    }
}
