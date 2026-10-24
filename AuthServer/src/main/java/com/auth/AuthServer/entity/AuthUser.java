package com.auth.AuthServer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="AuthUsers")
@NamedQueries({
        @NamedQuery(name = "Auth.findByEmail", query = "SELECT u FROM AuthUser u WHERE u.email = ?1"),
        @NamedQuery(name = "Auth.findByUserName", query = "SELECT u FROM AuthUser u WHERE u.userName = ?1")
})
public class AuthUser implements UserDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String userName;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String password;

    // new token column to persist current token (nullable)
    @Column(name = "token", length = 1000)
    private String token;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
//
//    public AuthUser( Long userId, String userName, String email, String role, String password, String token )
//    {
//        this.userId = userId;
//        this.userName = userName;
//        this.email = email;
//        this.role = role;
//        this.password = password;
//        this.token = token;
//    }
//
//    public Long getUserId()
//    {
//        return userId;
//    }
//
//    public void setUserId( Long userId )
//    {
//        this.userId = userId;
//    }
//
//    public String getUserName()
//    {
//        return userName;
//    }
//
//    public void setUserName( String userName )
//    {
//        this.userName = userName;
//    }
//
//    public String getEmail()
//    {
//        return email;
//    }
//
//    public void setEmail( String email )
//    {
//        this.email = email;
//    }
//
//    public String getRole()
//    {
//        return role;
//    }
//
//    public void setRole( String role )
//    {
//        this.role = role;
//    }
//
//    public void setPassword( String password )
//    {
//        this.password = password;
//    }
//
//    public String getToken()
//    {
//        return token;
//    }
//
//    public void setToken( String token )
//    {
//        this.token = token;
//    }
}