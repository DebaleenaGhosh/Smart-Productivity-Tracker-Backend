package com.user.UserService.dto;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto
{
    private Long id;
    private String username;
    private String email;
    private String password;
    private String role;
    private Long taskCount;
}