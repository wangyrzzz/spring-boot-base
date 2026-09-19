package com.example.demo.aop;

import com.example.demo.annotation.PreAuth;
import com.example.demo.common.ApiException;
import com.example.demo.common.AuthConstant;
import com.example.demo.common.AuthUserContext;
import com.example.demo.common.AuthenticatedUser;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.RbacPermissionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthAspectTest {

    @AfterEach
    void clearAuthentication() {
        AuthUserContext.clear();
    }

    @Test
    void classAnnotationAllowsAdministratorRole() {
        RbacPermissionService permissions = mock(RbacPermissionService.class);
        when(permissions.hasAnyRole("administrator", "admin")).thenReturn(true);
        AuthUserContext.set(AuthenticatedUser.builder().userId(1L).roleCodes(java.util.List.of("admin")).build());

        SecuredService service = proxy(new AuthAspect(permissions, mock(ApplicationContext.class)));

        assertEquals("admin", service.adminOnly());
    }

    @Test
    void classAnnotationDeniesNonAdministratorRole() {
        RbacPermissionService permissions = mock(RbacPermissionService.class);
        when(permissions.hasRole("admin")).thenReturn(false);
        AuthUserContext.set(AuthenticatedUser.builder().userId(1L).roleCodes(java.util.List.of("user")).build());

        SecuredService service = proxy(new AuthAspect(permissions, mock(ApplicationContext.class)));

        ApiException exception = assertThrows(ApiException.class, service::adminOnly);
        assertEquals(403, exception.getCode());
    }

    @Test
    void methodAnnotationDelegatesPermissionExpression() {
        RbacPermissionService permissions = mock(RbacPermissionService.class);
        when(permissions.permissionAll()).thenReturn(false);
        when(permissions.hasPermission("system:user:read")).thenReturn(true);
        AuthUserContext.set(AuthenticatedUser.builder().userId(1L).roleCodes(java.util.List.of("user")).build());

        PermissionSecuredService service = proxy(new PermissionSecuredService(),
                new AuthAspect(permissions, mock(ApplicationContext.class)));

        assertEquals("read", service.read());
    }

    @Test
    void permitAllExpressionDoesNotCallPermissionStore() {
        RbacPermissionService permissions = mock(RbacPermissionService.class);
        PermissionSecuredService service = proxy(new PermissionSecuredService(),
                new AuthAspect(permissions, mock(ApplicationContext.class)));

        assertEquals("public", service.publicEndpoint());
    }

    private SecuredService proxy(AuthAspect aspect) {
        return proxy(new SecuredService(), aspect);
    }

    private <T> T proxy(T target, AuthAspect aspect) {
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(aspect);
        @SuppressWarnings("unchecked")
        T proxy = (T) factory.getProxy();
        return proxy;
    }

    @PreAuth(AuthConstant.HAS_ROLE_ADMIN)
    static class SecuredService {
        public String adminOnly() {
            return "admin";
        }
    }

    static class PermissionSecuredService {
        @PreAuth(RbacPermissionCodes.USER_READ)
        public String read() {
            return "read";
        }

        @PreAuth(AuthConstant.PERMIT_ALL)
        public String publicEndpoint() {
            return "public";
        }
    }
}
