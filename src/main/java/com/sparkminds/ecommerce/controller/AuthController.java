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
import com.sparkminds.ecommerce.util.CookieUtil;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationServiceImpl authenticationService;
    private final CookieUtil cookieUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        AuthenticationResponse authResponse = authenticationService.register(request);
        cookieUtil.createCookie(response, CookieUtil.ACCESS_TOKEN_NAME, authResponse.getAccessToken());
        cookieUtil.createCookie(response, CookieUtil.REFRESH_TOKEN_NAME, authResponse.getRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthenticationResponse authResponse = authenticationService.login(request);
        cookieUtil.createCookie(response, CookieUtil.ACCESS_TOKEN_NAME, authResponse.getAccessToken());
        cookieUtil.createCookie(response, CookieUtil.REFRESH_TOKEN_NAME, authResponse.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request, HttpServletResponse response) {
        AuthenticationResponse authResponse = authenticationService.refreshToken(request.getRefreshToken());
        cookieUtil.createCookie(response, CookieUtil.ACCESS_TOKEN_NAME, authResponse.getAccessToken());
        cookieUtil.createCookie(response, CookieUtil.REFRESH_TOKEN_NAME, authResponse.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody(required = false) RefreshTokenRequest request, HttpServletResponse response) {
        if (request != null && request.getRefreshToken() != null) {
            authenticationService.logout(request.getRefreshToken());
        }
        cookieUtil.clearCookie(response, CookieUtil.ACCESS_TOKEN_NAME);
        cookieUtil.clearCookie(response, CookieUtil.REFRESH_TOKEN_NAME);
        return ResponseEntity.noContent().build();
    }
}

