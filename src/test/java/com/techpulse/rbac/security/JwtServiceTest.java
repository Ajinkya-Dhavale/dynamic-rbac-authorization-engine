package com.techpulse.rbac.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRET =
            "test-jwt-secret-key-with-at-least-32-characters";

    @Test
    void generatesValidTokenWithUsernameSubject() {
        JwtService jwtService = new JwtService(SECRET, 3600000L);

        String token = jwtService.generateToken("alice");

        assertTrue(jwtService.isTokenValid(token));
        assertEquals("alice", jwtService.extractUsername(token));
        assertEquals(3600000L, jwtService.expirationMs());
    }

    @Test
    void rejectsInvalidToken() {
        JwtService jwtService = new JwtService(SECRET, 3600000L);

        assertFalse(jwtService.isTokenValid("invalid-token"));
    }

    @Test
    void rejectsShortSecret() {
        assertThrows(IllegalStateException.class,
                () -> new JwtService("short", 3600000L));
    }

    @Test
    void rejectsNonPositiveExpiration() {
        assertThrows(IllegalStateException.class,
                () -> new JwtService(SECRET, 0L));
    }
}
