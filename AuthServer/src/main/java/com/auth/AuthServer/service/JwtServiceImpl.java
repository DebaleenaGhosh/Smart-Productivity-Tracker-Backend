package com.auth.AuthServer.service;

import java.util.Date;
import java.util.function.Function;

import com.auth.AuthServer.entity.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;

@Service
public class JwtServiceImpl implements JwtService
{
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private static final MacAlgorithm ALGORITHM = Jwts.SIG.HS512;

    private SecretKey getSigningKey()
    {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    private boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }

    public long getExpirationTime() {
        return expirationMs;
    }

    private Date extractExpiration(String token)
    {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token)
    {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token)
    {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public String generateToken(AuthUser user)
    {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getUserId())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), ALGORITHM)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails)
    {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
//    public boolean isTokenValid(String token)
//    {
//        try
//        {
//            Jwts.parser()
//                    .verifyWith((SecretKey) getSigningKey())
//                    .build()
//                    .parseSignedClaims(token);
//            return true;
//        }
//        catch (JwtException | IllegalArgumentException e)
//        {
//            return false;
//        }
//    }

//    @Override
//    public String extractUsername(String token)
//    {
//        var claims = Jwts.parser()
//                .verifyWith((SecretKey) getSigningKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//        return claims.getSubject();
//    }

//    public long getExpirationSeconds()
//    {
//        return expirationMs / 1000;
//    }
}
