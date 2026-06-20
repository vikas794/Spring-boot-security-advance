package com.example.security.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final List<SecurityConfigurer> configurers;

    // Constructor injection for modular configurers
    public SecurityConfig(List<SecurityConfigurer> configurers) {
        this.configurers = configurers;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Base security configuration
        http
            .csrf(AbstractHttpConfigurer::disable) // Defaulting to stateless API, CSRF disabled
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").hasAuthority("ROLE_ADMIN") // Restrict H2 Console to Admin
                .requestMatchers("/debug/**").permitAll()      // Allow debug endpoints for learning
                .requestMatchers("/auth/**").permitAll()       // Open auth endpoints
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable())); // Needed for H2 console

        // Dynamically apply all enabled modular configurers
        for (SecurityConfigurer configurer : configurers) {
            configurer.configure(http);
        }

        return http.build();
    }
}
