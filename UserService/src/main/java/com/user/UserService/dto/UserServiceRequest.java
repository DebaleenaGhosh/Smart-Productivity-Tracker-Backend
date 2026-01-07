package com.user.UserService.dto;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceRequest
{
    private Long userId;
    private String userName;
    private String email;
    private String role;
}
