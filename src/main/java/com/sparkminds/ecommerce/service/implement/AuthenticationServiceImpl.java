package com.sparkminds.ecommerce.service.implement;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sparkminds.ecommerce.dto.request.LoginRequest;
import com.sparkminds.ecommerce.dto.request.RegisterRequest;
import com.sparkminds.ecommerce.dto.response.AuthenticationResponse;
import com.sparkminds.ecommerce.entity.User;
import com.sparkminds.ecommerce.enumerator.Role;
import com.sparkminds.ecommerce.exception.BadRequestException;
import com.sparkminds.ecommerce.exception.ConflictResourceException;
import com.sparkminds.ecommerce.repository.UserRepository;
import com.sparkminds.ecommerce.security.CustomUserDetails;
import com.sparkminds.ecommerce.security.JwtService;
import com.sparkminds.ecommerce.service.AuthenticationService;
import com.sparkminds.ecommerce.service.RedisService;
import org.springframework.beans.factory.annotation.Value;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final RedisService redisService;
        
        @Value("${security.jwt.refresh-expiration}")
        private long jwtRefreshExpiration;

        public AuthenticationResponse register(RegisterRequest registerRequest) {
                // create user
                if (userRepository.existsByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail())) {
                        throw new ConflictResourceException("Username or Email is already taken");
                }
                User user = User.builder()
                                .username(registerRequest.getUsername())
                                .email(registerRequest.getEmail())
                                .password(passwordEncoder.encode(registerRequest.getPassword()))
                                .role(Role.USER) // Assign default role
                                .build();
                userRepository.save(user);

                CustomUserDetails userDetails = new CustomUserDetails(user);
                String accessToken = jwtService.generateAccessToken(userDetails);
                String refreshToken = jwtService.generateRefreshToken(userDetails);

                redisService.save(refreshToken, user.getUsername(), jwtRefreshExpiration, TimeUnit.MILLISECONDS);

                return AuthenticationResponse.builder()
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build();
        }

        public AuthenticationResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getUsername(),
                                                request.getPassword()));
                User user = userRepository.findByUsername(request.getUsername())
                                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
                CustomUserDetails userDetails = new CustomUserDetails(user);

                String accessToken = jwtService.generateAccessToken(userDetails);
                String refreshToken = jwtService.generateRefreshToken(userDetails);

                redisService.save(refreshToken, user.getUsername(), jwtRefreshExpiration, TimeUnit.MILLISECONDS);

                return AuthenticationResponse.builder()
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build();
        }

        public AuthenticationResponse refreshToken(String refreshToken) {
                // determine if present in Redis
                String storedUsername = redisService.get(refreshToken);
                if (storedUsername == null) {
                        throw new BadCredentialsException("Refresh token was not found in Redis or is expired");
                }

                String username = jwtService.extractUsername(refreshToken);
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new BadCredentialsException("Invalid or expired refresh token"));

                CustomUserDetails customUserDetails = new CustomUserDetails(user);

                if (!jwtService.isTokenValid(refreshToken, customUserDetails, "refresh")) {
                        throw new BadRequestException("Invalid refresh token usage");
                }
                String newAccessToken = jwtService.generateAccessToken(customUserDetails);

                return AuthenticationResponse.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(refreshToken)
                                .build();
        }

        public void logout(String refreshToken) {
                if (refreshToken != null) {
                        redisService.delete(refreshToken);
                }
        }
}
