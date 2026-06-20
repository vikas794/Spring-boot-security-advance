package com.example.security.apisecurity.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitFilterTest {

    @Test
    void shouldAllowRequestWhenUnderLimit() throws Exception {
        RateLimitFilter filter = new RateLimitFilter(2, 2, 60);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        // Verify filterChain was called
        assertThat(filterChain.getRequest()).isNotNull();
    }

    @Test
    void shouldRejectRequestWhenOverLimit() throws Exception {
        // Set capacity to 1
        RateLimitFilter filter = new RateLimitFilter(1, 1, 60);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockFilterChain filterChain = new MockFilterChain();

        // Consume the only token
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        filter.doFilterInternal(request, response1, filterChain);
        assertThat(response1.getStatus()).isEqualTo(HttpStatus.OK.value());

        // Second request should be rejected
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        filter.doFilterInternal(request, response2, filterChain);

        assertThat(response2.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
        assertThat(response2.getContentAsString()).isEqualTo("Too many requests");
    }
}
