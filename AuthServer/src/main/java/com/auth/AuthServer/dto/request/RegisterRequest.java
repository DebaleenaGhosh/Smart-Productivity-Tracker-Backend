package com.auth.AuthServer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest
{
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 to 30 characters")
    private String userName;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 100, message = "Password must be between 8 to 100 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Role is required")
    private String role;
}
