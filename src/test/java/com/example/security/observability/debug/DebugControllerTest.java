package com.example.security.observability.debug;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DebugControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DebugController()).build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getSecurityContext_whenAuthenticated_returnsContextInfo() throws Exception {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "testUser", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(get("/debug/security-context"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.principal").value("testUser"))
                .andExpect(jsonPath("$.isAuthenticated").value(true))
                .andExpect(jsonPath("$.authorities[0].authority").value("ROLE_USER"));
    }

    @Test
    void getSecurityContext_whenUnauthenticated_returnsStatusMessage() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/debug/security-context"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("No authentication found in context"));
    }

    @Test
    void getHeaders_returnsHeaders() throws Exception {
        mockMvc.perform(get("/debug/headers")
                .header("X-Test-Header", "TestValue")
                .header("User-Agent", "MockMvc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['X-Test-Header']").value("TestValue"))
                .andExpect(jsonPath("$['User-Agent']").value("MockMvc"));
    }
}
