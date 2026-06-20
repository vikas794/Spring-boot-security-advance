package com.example.security.auth.jwt;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    @Test
    void testNullAuthClaim() {
        JwtTokenProvider provider = new JwtTokenProvider("mySecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!!!", 3600000);

        String token = Jwts.builder()
                .subject("user")
                // no auth claim
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("mySecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!!!".getBytes()))
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .compact();

        Authentication auth = provider.getAuthentication(token);
        assertTrue(auth.getAuthorities().isEmpty());
    }

    @Test
    void testEmptyAuthClaim() {
        JwtTokenProvider provider = new JwtTokenProvider("mySecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!!!", 3600000);

        String token = Jwts.builder()
                .subject("user")
                .claim("auth", "") // empty auth claim
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("mySecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!!!".getBytes()))
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .compact();

        Authentication auth = provider.getAuthentication(token);
        assertTrue(auth.getAuthorities().isEmpty());
    }
}
