package com.example.security.apisecurity.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@ConditionalOnProperty(name = "security.modules.rate-limit.enabled", havingValue = "true")
public class RateLimitFilter extends OncePerRequestFilter {

    private final Bucket bucket;

    public RateLimitFilter(
            @Value("${security.modules.rate-limit.capacity:10}") long capacity,
            @Value("${security.modules.rate-limit.refill-tokens:10}") long refillTokens,
            @Value("${security.modules.rate-limit.refill-duration-seconds:60}") long refillDurationSeconds) {

        Refill refill = Refill.intervally(refillTokens, Duration.ofSeconds(refillDurationSeconds));
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        this.bucket = Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests");
        }
    }
}
