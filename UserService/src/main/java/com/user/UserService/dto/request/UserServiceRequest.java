package com.user.UserService.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceRequest
{
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 30)
    private String userName;
    @Email(message = "Invalid email format")
    private String email;
    private String role;
}
