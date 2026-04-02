package com.sparkminds.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username must be filled")
    private String username;

    @NotBlank(message = "Email must be filled")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password must be filled")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters with uppercase and number")
    private String password;
}
