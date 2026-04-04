# Security Learning Platform - Extension Guide

## Architecture Overview
This project is designed as a **modular, extensible, production-grade playground** for mastering Spring Security. It avoids a monolithic `SecurityConfig` by allowing individual security modules (JWT, Rate Limiting, OAuth2, etc.) to inject their own configurations dynamically.

## Core Concepts

### 1. Modular Configuration
The `SecurityConfig` class accepts a list of `SecurityConfigurer` implementations. Each module provides a class implementing this interface.
By using `@ConditionalOnProperty`, we can toggle entire security layers from `application.yml`.

### 2. The Filter Chain
Spring Security operates on a chain of filters. In this architecture:
1. **RateLimitFilter (Bucket4j)**: Evaluated first to drop excessive traffic before expensive security operations.
2. **JwtAuthenticationFilter**: Extracts the Bearer token, validates it using `JwtTokenProvider`, and sets the `Authentication` in the `SecurityContext`.
3. **Standard Spring Security Filters**: Handles anonymous users, exception translation, and authorization.

### 3. Observability
Use the `/debug/security-context` endpoint to inspect what Spring Security sees for the current request.

## How to Add a New Module

1. **Create the Package**: e.g., `com.example.security.auth.mfa`.
2. **Create the Filter**: Extend `OncePerRequestFilter`.
3. **Create the Configurer**: Implement `SecurityConfigurer`.
    ```java
    @Component
    @ConditionalOnProperty(name = "security.modules.mfa.enabled", havingValue = "true")
    public class MfaSecurityConfigurer implements SecurityConfigurer {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            // Add your filter to the chain
            // http.addFilterAfter(new MfaFilter(), JwtAuthenticationFilter.class);
        }
    }
    ```
4. **Update `application.yml`**: Add the property `security.modules.mfa.enabled: true`.

## Zero Trust and Hardening
- By default, `anyRequest().authenticated()` is enforced.
- Secrets must not be hardcoded (use Vault or environment variables in production).
- CSRF is disabled for the stateless API by default, but can be configured if needed for browser-based clients.
