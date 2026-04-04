package com.example.security.apisecurity.ratelimit;

import com.example.security.core.config.SecurityConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "security.modules.rate-limit.enabled", havingValue = "true")
public class RateLimitConfigurer implements SecurityConfigurer {

    private final RateLimitFilter rateLimitFilter;

    public RateLimitConfigurer(RateLimitFilter rateLimitFilter) {
        this.rateLimitFilter = rateLimitFilter;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // Place rate limit filter early in the chain
        http.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
