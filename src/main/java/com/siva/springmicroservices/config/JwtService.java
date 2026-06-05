package com.siva.springmicroservices.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secretKey.getBytes()
        );
    }

    public String generateToken(
            UserDetails userDetails) {

        Date expiryDate = getExpirationDate();
        return Jwts.builder()
                .claim(
                        "role",
                        userDetails.getAuthorities()
                )
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        String username =
                extractUsername(token);

        return username.equals(
                userDetails.getUsername())
                && !isTokenExpired(token);
    }

    public Date extractExpiration(
            String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    public long getExpirationTime() {
        return expiration;
    }

    private boolean isTokenExpired(
            String token) {

        return extractExpiration(token)
                .before(new Date());
    }
    public Date getExpirationDate() {
        return new Date(
                System.currentTimeMillis() + expiration
        );
    }
}