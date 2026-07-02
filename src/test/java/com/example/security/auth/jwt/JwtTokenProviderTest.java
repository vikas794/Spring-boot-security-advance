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

    @Test
    void testExpiredJwtException() {
        JwtTokenProvider provider = new JwtTokenProvider("mySecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!!!", 3600000);
        String token = Jwts.builder()
                .subject("user")
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("mySecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!!!".getBytes()))
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .compact();
        assertNull(provider.validateAndGetAuthentication(token));
    }

    @Test
    void testSignatureException() {
        JwtTokenProvider provider = new JwtTokenProvider("mySecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!!!", 3600000);
        String token = Jwts.builder()
                .subject("user")
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("anotherSecretKeyThatIsAlsoAtLeast32BytesLong!!".getBytes()))
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .compact();
        assertNull(provider.validateAndGetAuthentication(token));
    }

    @Test
    void testMalformedJwtException() {
        JwtTokenProvider provider = new JwtTokenProvider("mySecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!!!", 3600000);
        assertNull(provider.validateAndGetAuthentication("this.is.malformed"));
    }

    @Test
    void testUnsupportedJwtException() {
        JwtTokenProvider provider = new JwtTokenProvider("mySecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!!!", 3600000);
        String token = Jwts.builder()
                .subject("user")
                .compact(); // Unsecured JWT
        assertNull(provider.validateAndGetAuthentication(token));
    }

    @Test
    void testIllegalArgumentException() {
        JwtTokenProvider provider = new JwtTokenProvider("mySecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!!!", 3600000);
        assertNull(provider.validateAndGetAuthentication(""));
        assertNull(provider.validateAndGetAuthentication(null));
    }

    @Test
    void testValidateTokenValid() {
        JwtTokenProvider provider = new JwtTokenProvider("mySecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!!!", 3600000);

        String token = Jwts.builder()
                .subject("user")
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("mySecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!!!".getBytes()))
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .compact();

        assertTrue(provider.validateToken(token));
    }

    @Test
    void testValidateTokenExpired() {
        JwtTokenProvider provider = new JwtTokenProvider("mySecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!!!", -3600000); // Expired 1 hour ago

        String token = Jwts.builder()
                .subject("user")
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("mySecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!!!".getBytes()))
                .expiration(new Date(System.currentTimeMillis() - 3600000)) // Already expired
                .compact();

        assertFalse(provider.validateToken(token));
    }

    @Test
    void testValidateTokenMalformed() {
        JwtTokenProvider provider = new JwtTokenProvider("mySecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!!!", 3600000);

        assertFalse(provider.validateToken("invalid.malformed.token"));
    }

    @Test
    void testValidateTokenInvalidSignature() {
        JwtTokenProvider provider = new JwtTokenProvider("mySecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!!!", 3600000);

        String token = Jwts.builder()
                .subject("user")
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("anotherSecretKeyThatIsAtLeast32BytesLongForHmacSha256!!!".getBytes()))
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .compact();

        assertFalse(provider.validateToken(token));
    }
}
