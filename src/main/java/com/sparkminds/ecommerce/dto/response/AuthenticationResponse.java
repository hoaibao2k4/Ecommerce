package com.sparkminds.ecommerce.dto.response;


import com.sparkminds.ecommerce.enumerator.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String username;
    private String email;
    private Role role;
    private String accessToken;
    private String refreshToken;
}
