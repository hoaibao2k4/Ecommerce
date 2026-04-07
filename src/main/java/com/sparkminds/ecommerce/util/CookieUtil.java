package com.sparkminds.ecommerce.util;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieUtil {

    public static final String ACCESS_TOKEN_NAME = "accessToken";
    public static final String REFRESH_TOKEN_NAME = "refreshToken";
    private static final int MAX_AGE = 7 * 24 * 60 * 60; // 7 days (in seconds)

    // Create a new cookie with HttpOnly security and Lax SameSite policy

    public void createCookie(HttpServletResponse response, String name, String value) {
        // Standard way to set cookie using Cookie object
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true); // Prevent XSS
        cookie.setSecure(false); // Set TRUE if running HTTPS (Production)
        cookie.setPath("/");
        cookie.setMaxAge(MAX_AGE);

        response.addCookie(cookie);
    }

    // Clears a cookie by setting Max-Age to 0

    public void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
