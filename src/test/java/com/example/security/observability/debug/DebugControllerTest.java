package com.example.security.observability.debug;

import com.example.security.core.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.jupiter.api.BeforeEach;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class DebugControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

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
}
