package com.example.security.observability.debug;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class DebugControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private MockMvc standaloneMockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        standaloneMockMvc = MockMvcBuilders.standaloneSetup(new DebugController()).build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // --- Integration Tests (Security Layer) ---

    @Test
    public void unauthenticatedAccessToDebug_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/debug/security-context"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    public void authenticatedUserWithoutAdminRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/debug/security-context"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void authenticatedAdmin_ShouldSucceed() throws Exception {
        mockMvc.perform(get("/debug/security-context"))
                .andExpect(status().isOk());
    }

    @Test
    public void unauthenticatedAccessToHeaders_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/debug/headers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    public void authenticatedUserWithoutAdminRoleForHeaders_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/debug/headers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    public void authenticatedAdminForHeaders_ShouldSucceed() throws Exception {
        mockMvc.perform(get("/debug/headers"))
                .andExpect(status().isOk());
    }

    // --- Unit Tests (Controller Logic) ---

    @Test
    void getSecurityContext_whenAuthenticated_returnsContextInfo() throws Exception {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "testUser", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        standaloneMockMvc.perform(get("/debug/security-context"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.principal").value("testUser"))
                .andExpect(jsonPath("$.isAuthenticated").value(true))
                .andExpect(jsonPath("$.authorities[0].authority").value("ROLE_USER"));
    }

    @Test
    void getSecurityContext_whenUnauthenticated_returnsStatusMessage() throws Exception {
        SecurityContextHolder.clearContext();

        standaloneMockMvc.perform(get("/debug/security-context"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("No authentication found in context"));
    }

    @Test
    void getHeaders_returnsHeaders() throws Exception {
        standaloneMockMvc.perform(get("/debug/headers")
                .header("X-Test-Header", "TestValue")
                .header("User-Agent", "MockMvc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['X-Test-Header']").value("TestValue"))
                .andExpect(jsonPath("$['User-Agent']").value("MockMvc"));
    }
}
