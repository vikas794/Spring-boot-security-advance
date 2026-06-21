package com.example.security.auth.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Arrays;

public class JwtTokenProviderPerfTest {
    @Test
    public void testPerformance() {
        JwtTokenProvider provider = new JwtTokenProvider(
            "my-very-secret-key-that-needs-to-be-at-least-256-bits-long-so-here-is-some-more-text",
            3600000
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(
            "user", "password", Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        String token = provider.createToken(auth);

        // Warmup
        for (int i = 0; i < 10000; i++) {
            provider.getAuthentication(token);
            provider.createToken(auth);
        }

        // Measure Create
        long startCreate = System.nanoTime();
        for (int i = 0; i < 50000; i++) {
            provider.createToken(auth);
        }
        long endCreate = System.nanoTime();

        // Measure Parse
        long startParse = System.nanoTime();
        for (int i = 0; i < 50000; i++) {
            provider.getAuthentication(token);
        }
        long endParse = System.nanoTime();

        System.out.println("====== PERF MEASURE ======");
        System.out.println("Create time: " + (endCreate - startCreate) / 1_000_000 + " ms");
        System.out.println("Parse time: " + (endParse - startParse) / 1_000_000 + " ms");
        System.out.println("==========================");
    }
}
