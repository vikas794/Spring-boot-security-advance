package com.example.security.auth.jwt;

import com.example.security.core.config.SecurityConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "security.modules.jwt.enabled", havingValue = "true")
public class JwtSecurityConfigurer implements SecurityConfigurer {

    private final JwtTokenProvider tokenProvider;

    public JwtSecurityConfigurer(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter customFilter = new JwtAuthenticationFilter(tokenProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
