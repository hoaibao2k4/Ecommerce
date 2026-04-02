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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

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

                // Convert inside CustomUserDetails for Jwt generation
                return AuthenticationResponse.builder()
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .build();
        }

        public AuthenticationResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getUsername(),
                                                request.getPassword()));

                // authenManager flow:
                // 1. create tmp obj UsernamePasswordAuthenticationToken
                // 2. call provider (DaoProvider) to load user from db
                // 3. compare password
                // 4. return obj if correct

                // If authen correctly
                User user = userRepository.findByUsername(request.getUsername())
                                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
                CustomUserDetails userDetails = new CustomUserDetails(user);

                String accessToken = jwtService.generateAccessToken(userDetails);
                // refresh is not stored in db (just create)
                String refreshToken = jwtService.generateRefreshToken(userDetails);

                return AuthenticationResponse.builder()
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build();
        }

        public AuthenticationResponse refreshToken(String refreshToken) {
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
}
