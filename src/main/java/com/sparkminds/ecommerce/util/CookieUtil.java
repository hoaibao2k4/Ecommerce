package com.sparkminds.ecommerce.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieUtil {

    public static final String ACCESS_TOKEN_NAME = "accessToken";
    public static final String REFRESH_TOKEN_NAME = "refreshToken";
    private static final int MAX_AGE = 7 * 24 * 60 * 60; // 7 days (in seconds)

    // Create a new cookie with HttpOnly security and cross-site support
    public void createCookie(HttpServletResponse response, String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true) // Prevent XSS
                .secure(true) // Required for SameSite=None
                .path("/")
                .maxAge(MAX_AGE)
                .sameSite("None") // Allow cross-site or cross-origin requests
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // Clears a cookie by setting Max-Age to 0
    public void clearCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
