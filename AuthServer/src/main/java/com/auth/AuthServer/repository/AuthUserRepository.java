package com.auth.AuthServer.repository;

import com.auth.AuthServer.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long>
{
    @Query(name = "Auth.findByEmail")
    Optional<AuthUser> findByEmail(String email);
    @Query(name = "Auth.findByUserName")
    Optional<AuthUser> findByUserName(String username);
    Optional<AuthUser> findByToken(String token);
}
