package com.example.security.core.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface SecurityConfigurer {
    void configure(HttpSecurity http) throws Exception;
}
