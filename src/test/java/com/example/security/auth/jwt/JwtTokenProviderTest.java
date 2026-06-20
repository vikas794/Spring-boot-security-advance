package com.example.security.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private String secret;
    private SecretKey key;

    @BeforeEach
    public void setUp() {
        secret = "ThisIsAVeryLongSecretKeyThatIsAtLeast32BytesLong!";
        long expiration = 3600000; // 1 hour
        jwtTokenProvider = new JwtTokenProvider(secret, expiration);
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Test
    public void testValidateToken_ValidToken_ReturnsTrue() {
        User principal = new User("testuser", "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());

        String token = jwtTokenProvider.createToken(authentication);

        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    public void testValidateToken_InvalidToken_ReturnsFalse() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.string"));
    }

    @Test
    public void testValidateToken_EmptyToken_ReturnsFalse() {
        assertFalse(jwtTokenProvider.validateToken(""));
    }

    @Test
    public void testValidateToken_ExpiredToken_ReturnsFalse() {
        // Create an expired token manually
        long now = (new Date()).getTime();
        Date validity = new Date(now - 10000); // 10 seconds ago

        String expiredToken = Jwts.builder()
                .subject("testuser")
                .claim("auth", "ROLE_USER")
                .signWith(key)
                .expiration(validity)
                .compact();

        assertFalse(jwtTokenProvider.validateToken(expiredToken));
    }

    @Test
    public void testValidateToken_MalformedToken_ReturnsFalse() {
        // A valid token structure but malformed
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJoZWxsbyI6IndvcmxkIn0.";
        assertFalse(jwtTokenProvider.validateToken(token));
    }

    @Test
    public void testValidateToken_WrongSignature_ReturnsFalse() {
        // Create a token with a different secret
        String differentSecret = "AnotherVeryLongSecretKeyThatIsAtLeast32BytesLong!";
        SecretKey differentKey = Keys.hmacShaKeyFor(differentSecret.getBytes());

        long now = (new Date()).getTime();
        Date validity = new Date(now + 3600000); // 1 hour valid

        String tokenWithWrongSignature = Jwts.builder()
                .subject("testuser")
                .claim("auth", "ROLE_USER")
                .signWith(differentKey)
                .expiration(validity)
                .compact();

        assertFalse(jwtTokenProvider.validateToken(tokenWithWrongSignature));
    }
}
