package com.sparkminds.ecommerce.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparkminds.ecommerce.dto.request.LoginRequest;
import com.sparkminds.ecommerce.dto.request.RefreshTokenRequest;
import com.sparkminds.ecommerce.dto.request.RegisterRequest;
import com.sparkminds.ecommerce.dto.response.AuthenticationResponse;
import com.sparkminds.ecommerce.service.implement.AuthenticationServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationServiceImpl authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthenticationResponse authResponse = authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthenticationResponse authResponse = authenticationService.login(request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthenticationResponse authResponse = authenticationService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }
}
