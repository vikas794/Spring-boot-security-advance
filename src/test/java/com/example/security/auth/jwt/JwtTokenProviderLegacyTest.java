package com.example.security.auth.jwt;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtTokenProviderLegacyTest {
    @Test
    public void testLegacyToken() {
        JwtTokenProvider provider = new JwtTokenProvider(
            "my-very-secret-key-that-needs-to-be-at-least-256-bits-long-so-here-is-some-more-text",
            3600000
        );

        String legacyToken = Jwts.builder()
                .subject("legacy-user")
                .claim("auth", "ROLE_USER,ROLE_ADMIN")
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("my-very-secret-key-that-needs-to-be-at-least-256-bits-long-so-here-is-some-more-text".getBytes()))
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .compact();

        Authentication auth = provider.getAuthentication(legacyToken);
        assertEquals("legacy-user", auth.getName());
        assertEquals(2, auth.getAuthorities().size());
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}
