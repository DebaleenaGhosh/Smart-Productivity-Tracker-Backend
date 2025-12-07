package com.user.UserService.repository;

import com.user.UserService.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserProfile, Long>
{
    Optional<UserProfile> findByEmail(String email);
    Optional<UserProfile> findByUsername(String username);
}