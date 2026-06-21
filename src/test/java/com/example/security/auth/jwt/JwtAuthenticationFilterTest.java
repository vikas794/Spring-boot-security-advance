package com.example.security.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_validJwtToken_setsAuthentication() throws ServletException, IOException {
        String token = "valid.jwt.token";
        request.addHeader(JwtAuthenticationFilter.AUTHORIZATION_HEADER, "Bearer " + token);

        when(tokenProvider.validateAndGetAuthentication(token)).thenReturn(authentication);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authentication);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_invalidJwtToken_doesNotSetAuthentication() throws ServletException, IOException {
        String token = "invalid.jwt.token";
        request.addHeader(JwtAuthenticationFilter.AUTHORIZATION_HEADER, "Bearer " + token);

        when(tokenProvider.validateAndGetAuthentication(token)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_missingAuthorizationHeader_doesNotSetAuthentication() throws ServletException, IOException {
        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(tokenProvider, never()).validateAndGetAuthentication(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_missingBearerPrefix_doesNotSetAuthentication() throws ServletException, IOException {
        String token = "valid.jwt.token";
        request.addHeader(JwtAuthenticationFilter.AUTHORIZATION_HEADER, token);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(tokenProvider, never()).validateAndGetAuthentication(anyString());
        verify(filterChain).doFilter(request, response);
    }
}
