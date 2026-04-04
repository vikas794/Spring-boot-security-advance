package com.example.security.observability.debug;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/security-context")
    public Map<String, Object> getSecurityContext() {
        Map<String, Object> contextInfo = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            contextInfo.put("principal", authentication.getPrincipal());
            contextInfo.put("authorities", authentication.getAuthorities());
            contextInfo.put("isAuthenticated", authentication.isAuthenticated());
            contextInfo.put("details", authentication.getDetails());
        } else {
            contextInfo.put("status", "No authentication found in context");
        }

        return contextInfo;
    }

    @GetMapping("/headers")
    public Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        request.getHeaderNames().asIterator().forEachRemaining(
                headerName -> headers.put(headerName, request.getHeader(headerName))
        );
        return headers;
    }
}
