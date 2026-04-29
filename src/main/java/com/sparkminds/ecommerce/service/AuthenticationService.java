package com.sparkminds.ecommerce.service;

import com.sparkminds.ecommerce.dto.request.LoginRequest;
import com.sparkminds.ecommerce.dto.request.RegisterRequest;
import com.sparkminds.ecommerce.dto.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest registerRequest);
    AuthenticationResponse login(LoginRequest request);
    AuthenticationResponse refreshToken(String refreshToken);
    void logout(String refreshToken);
}
