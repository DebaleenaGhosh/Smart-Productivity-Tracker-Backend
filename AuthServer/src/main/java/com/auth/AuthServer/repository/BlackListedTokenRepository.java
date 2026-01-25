package com.auth.AuthServer.repository;

import com.auth.AuthServer.entity.BlackListedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;

public interface BlackListedTokenRepository extends JpaRepository<BlackListedToken, Long>
{
    boolean existsByToken(String token);
    void deleteByExpiryBefore(Instant time);
}
