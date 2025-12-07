package com.user.UserService.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Users")
public class UserProfile
{
    @Id
    @GeneratedValue
    private Long userId;
    private String username;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private int taskCount;

    public enum Role
    {
        USER,           // Regular end-user
        PREMIUM_USER,   // Users with access to premium AI features
        ADMIN,          // Admin for user and data management
        MODERATOR       // Can review flagged tasks/suggestions
    }
}