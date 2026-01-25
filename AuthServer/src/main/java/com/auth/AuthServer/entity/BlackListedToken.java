package com.auth.AuthServer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "blacklisted_tokens", indexes = @Index(columnList = "token", name = "idx_token"))
public class BlackListedToken
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String token;

    @Column(nullable = false)
    private Instant expiry;
}
