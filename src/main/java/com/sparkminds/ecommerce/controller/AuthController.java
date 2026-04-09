package com.sparkminds.ecommerce.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
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
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @CookieValue(name = CookieUtil.REFRESH_TOKEN_NAME) String refreshToken,
            HttpServletResponse response) {
        
        AuthenticationResponse authResponse = authenticationService.refreshToken(refreshToken);
        cookieUtil.createCookie(response, CookieUtil.ACCESS_TOKEN_NAME, authResponse.getAccessToken());
        // cookieUtil.createCookie(response, CookieUtil.REFRESH_TOKEN_NAME, authResponse.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = CookieUtil.REFRESH_TOKEN_NAME, required = false) String refreshToken,
            HttpServletResponse response) {

        // If the token exists, invalidate it in Redis/DB. If not, proceed to clear cookies.
        if (refreshToken != null) {
            authenticationService.logout(refreshToken);
        }
        
        cookieUtil.clearCookie(response, CookieUtil.ACCESS_TOKEN_NAME);
        cookieUtil.clearCookie(response, CookieUtil.REFRESH_TOKEN_NAME);
        return ResponseEntity.noContent().build();
    }
}

