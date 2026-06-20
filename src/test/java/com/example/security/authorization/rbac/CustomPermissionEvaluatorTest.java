package com.example.security.authorization.rbac;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomPermissionEvaluatorTest {

    private CustomPermissionEvaluator evaluator;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        evaluator = new CustomPermissionEvaluator();
    }

    // --- Tests for hasPermission(Authentication, Object, Object) ---

    @Test
    void hasPermission_ObjectTarget_NullAuthentication_ReturnsFalse() {
        assertFalse(evaluator.hasPermission(null, new Object(), "READ"));
    }

    @Test
    void hasPermission_ObjectTarget_NullTarget_ReturnsFalse() {
        assertFalse(evaluator.hasPermission(authentication, null, "READ"));
    }

    @Test
    void hasPermission_ObjectTarget_NonStringPermission_ReturnsFalse() {
        assertFalse(evaluator.hasPermission(authentication, new Object(), 123));
    }

    @Test
    void hasPermission_ObjectTarget_UserHasRequiredAuthority_ReturnsTrue() {
        Object targetDomainObject = new Object(); // Class name is "Object", upper cased is "OBJECT"
        String permission = "read";

        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(authority.getAuthority()).thenReturn("OBJECT_READ");
        doReturn(List.of(authority)).when(authentication).getAuthorities();

        assertTrue(evaluator.hasPermission(authentication, targetDomainObject, permission));
    }

    @Test
    void hasPermission_ObjectTarget_UserHasAdminRole_ReturnsTrue() {
        Object targetDomainObject = new Object();
        String permission = "write";

        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(authority.getAuthority()).thenReturn("ROLE_ADMIN");
        doReturn(List.of(authority)).when(authentication).getAuthorities();

        assertTrue(evaluator.hasPermission(authentication, targetDomainObject, permission));
    }

    @Test
    void hasPermission_ObjectTarget_UserLacksAuthority_ReturnsFalse() {
        Object targetDomainObject = new Object();
        String permission = "delete";

        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(authority.getAuthority()).thenReturn("OBJECT_READ");
        doReturn(List.of(authority)).when(authentication).getAuthorities();

        assertFalse(evaluator.hasPermission(authentication, targetDomainObject, permission));
    }

    // --- Tests for hasPermission(Authentication, Serializable, String, Object) ---

    @Test
    void hasPermission_IdAndType_NullAuthentication_ReturnsFalse() {
        assertFalse(evaluator.hasPermission(null, 1L, "TargetType", "READ"));
    }

    @Test
    void hasPermission_IdAndType_NullTargetType_ReturnsFalse() {
        assertFalse(evaluator.hasPermission(authentication, 1L, null, "READ"));
    }

    @Test
    void hasPermission_IdAndType_NonStringPermission_ReturnsFalse() {
        assertFalse(evaluator.hasPermission(authentication, 1L, "TargetType", 123));
    }

    @Test
    void hasPermission_IdAndType_UserHasRequiredAuthority_ReturnsTrue() {
        String targetType = "TargetType"; // upper cased is "TARGETTYPE"
        String permission = "read";

        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(authority.getAuthority()).thenReturn("TARGETTYPE_READ");
        doReturn(List.of(authority)).when(authentication).getAuthorities();

        assertTrue(evaluator.hasPermission(authentication, 1L, targetType, permission));
    }

    @Test
    void hasPermission_IdAndType_UserHasAdminRole_ReturnsTrue() {
        String targetType = "TargetType";
        String permission = "write";

        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(authority.getAuthority()).thenReturn("ROLE_ADMIN");
        doReturn(List.of(authority)).when(authentication).getAuthorities();

        assertTrue(evaluator.hasPermission(authentication, 1L, targetType, permission));
    }

    @Test
    void hasPermission_IdAndType_UserLacksAuthority_ReturnsFalse() {
        String targetType = "TargetType";
        String permission = "delete";

        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(authority.getAuthority()).thenReturn("TARGETTYPE_READ");
        doReturn(List.of(authority)).when(authentication).getAuthorities();

        assertFalse(evaluator.hasPermission(authentication, 1L, targetType, permission));
    }
}