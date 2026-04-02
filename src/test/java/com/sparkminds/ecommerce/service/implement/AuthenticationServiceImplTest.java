package com.sparkminds.ecommerce.service.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sparkminds.ecommerce.dto.request.LoginRequest;
import com.sparkminds.ecommerce.dto.request.RegisterRequest;
import com.sparkminds.ecommerce.dto.response.AuthenticationResponse;
import com.sparkminds.ecommerce.entity.User;
import com.sparkminds.ecommerce.enumerator.Role;
import com.sparkminds.ecommerce.exception.BadRequestException;
import com.sparkminds.ecommerce.exception.ConflictResourceException;
import com.sparkminds.ecommerce.repository.UserRepository;
import com.sparkminds.ecommerce.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private JwtService jwtService;

        @Mock
        private AuthenticationManager authenticationManager;

        @InjectMocks
        private AuthenticationServiceImpl authenticationService;

        private User mockUser;
        private RegisterRequest registerRequest;
        private LoginRequest loginRequest;

        @BeforeEach
        void setUp() {
                mockUser = User.builder()
                                .id(1L)
                                .username("testuser")
                                .email("test@example.com")
                                .password("encodedPassword")
                                .role(Role.USER)
                                .build();

                registerRequest = RegisterRequest.builder()
                                .username("testuser")
                                .email("test@example.com")
                                .password("password123")
                                .build();

                loginRequest = LoginRequest.builder()
                                .username("testuser")
                                .password("password123")
                                .build();
        }

    // Test successful registration with valid information
    @Test
    void register_correctInformation_returnsAuthentication() {
        // arrange
        when(userRepository.existsByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail()))
                .thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

        // action
        AuthenticationResponse response = authenticationService.register(registerRequest);

        // assert
        assertEquals(registerRequest.getUsername(), response.getUsername());
        assertEquals(registerRequest.getEmail(), response.getEmail());
        assertEquals(Role.USER, response.getRole());
        verify(userRepository).save(any());
    }

    // Test registration failure when username or email is already taken
    @Test
    void register_whenUsernameOrEmailTaken_throwsException() {
        // arrange
        when(userRepository.existsByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail()))
                .thenReturn(true);

        // action
        ConflictResourceException ex = assertThrows(ConflictResourceException.class,
                () -> authenticationService.register(registerRequest));

        // assert
        assertEquals("Username or Email is already taken", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // Test successful login, returning access and refresh tokens
    @Test
    void login_correctInformation_returnsToken() {
        // arrange
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(mockUser));
        when(jwtService.generateAccessToken(any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

        // action
        AuthenticationResponse response = authenticationService.login(loginRequest);

        // assert
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("testuser", response.getUsername());
        verify(authenticationManager).authenticate(any());
    }

    // Test login failure when user is not found or credentials are invalid
    @Test
    void login_invalidUser_throwsBadCredentialsException() {
        // arrange
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        // action
        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> authenticationService.login(loginRequest));

        // assert
        assertEquals("Invalid username or password", ex.getMessage());
    }

    // Test refresh token failure when token is invalid or expired
    @Test
    void refreshToken_invalidToken_throwsException() {
        // arrange
        String invalidToken = "invalid-token";
        when(jwtService.extractUsername(invalidToken)).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(jwtService.isTokenValid(any(), any(), any())).thenReturn(false);

        // action
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> authenticationService.refreshToken(invalidToken));

        // assert
        assertEquals("Invalid refresh token usage", ex.getMessage());
    }
}
