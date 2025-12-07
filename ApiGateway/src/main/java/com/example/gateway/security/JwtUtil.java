//package com.example.gateway.security;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import java.nio.charset.StandardCharsets;
//
//@Component
//public class JwtUtil
//{
//    @Value("${jwt.secret:secret-key}")
//    private String secret;
//
//    public Claims parseToken(String token) {
//        return Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
//                .build().parseClaimsJws(token).getBody();
//    }
//}
