package com.techpulse.rbac.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

    private static final Logger log =
            LoggerFactory.getLogger(JwtService.class);

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        if (secret == null || secret.trim().length() < 32) {
            throw new IllegalStateException(
                    "JWT secret must contain at least 32 characters");
        }
        if (expirationMs <= 0) {
            throw new IllegalStateException(
                    "JWT expiration must be greater than zero");
        }
        this.expirationMs = expirationMs;
        this.signingKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));
        log.info("JWT service initialized");
    }

    public String generateToken(String username) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(
                issuedAt.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(signingKey)
                .compact();
    }

    public long expirationMs() {
        return expirationMs;
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }
}
