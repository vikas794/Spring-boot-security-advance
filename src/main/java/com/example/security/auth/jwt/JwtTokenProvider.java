package com.example.security.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "security.modules.jwt.enabled", havingValue = "true")
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey key;
    private final long tokenValidityInMilliseconds;

    public JwtTokenProvider(
            @Value("${security.modules.jwt.secret}") String secret,
            @Value("${security.modules.jwt.expiration}") long tokenValidityInMilliseconds) {
        // Uses strong base64-encoded secret
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
    }

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .subject(authentication.getName())
                .claim("auth", authorities)
                .signWith(key)
                .expiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Collection<? extends GrantedAuthority> authorities;
        Object authClaim = claims.get("auth");
        if (authClaim == null) {
            authorities = java.util.Collections.emptyList();
        } else {
            authorities = Arrays.stream(authClaim.toString().split(","))
                    .filter(auth -> !auth.trim().isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
            return true;
        } catch (Exception e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public Authentication validateAndGetAuthentication(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get("auth").toString().split(","))
                            .filter(auth -> !auth.trim().isEmpty())
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            org.springframework.security.core.userdetails.User principal =
                    new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);

            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            org.slf4j.LoggerFactory.getLogger(JwtTokenProvider.class).error("Expired JWT token", e);
            return null;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            org.slf4j.LoggerFactory.getLogger(JwtTokenProvider.class).error("Invalid JWT signature", e);
            return null;
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            org.slf4j.LoggerFactory.getLogger(JwtTokenProvider.class).error("Invalid JWT token", e);
            return null;
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            org.slf4j.LoggerFactory.getLogger(JwtTokenProvider.class).error("Unsupported JWT token", e);
            return null;
        } catch (IllegalArgumentException e) {
            org.slf4j.LoggerFactory.getLogger(JwtTokenProvider.class).error("JWT claims string is empty", e);
            return null;
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(JwtTokenProvider.class).error("Failed to parse JWT token", e);
            return null;
        }
    }
}
