package com.sparkminds.ecommerce.service;

import com.sparkminds.ecommerce.dto.response.AuthenticationResponse;
import com.sparkminds.ecommerce.entity.User;

public interface UserService {
    public User getCurrentUser();

    public AuthenticationResponse getUserInfo();
}
