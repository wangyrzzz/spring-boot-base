package com.example.demo.common;

import java.time.LocalTime;

/** SpEL root object exposed to {@link com.example.demo.annotation.PreAuth}. */
public class AuthExpressionRoot {
    private final RbacPermissionService permissionService;

    public AuthExpressionRoot(RbacPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public boolean permissionAll() {
        return permissionService.permissionAll();
    }

    public boolean hasPermission(String permission) {
        return permissionService.hasPermission(permission);
    }

    public boolean permitAll() {
        return true;
    }

    public boolean denyAll() {
        return false;
    }

    public boolean hasAuth() {
        return AuthUserContext.get() != null;
    }

    public boolean hasRole(String role) {
        return permissionService.hasRole(role);
    }

    public boolean hasAnyRole(String... roles) {
        return permissionService.hasAnyRole(roles);
    }

    public boolean hasAllRole(String... roles) {
        return permissionService.hasAllRole(roles);
    }

    public boolean hasAllRoles(String... roles) {
        return hasAllRole(roles);
    }

    public boolean hasTimeAuth(Integer startHour, Integer endHour) {
        if (startHour == null || endHour == null) {
            return false;
        }
        int hour = LocalTime.now().getHour();
        return hour >= startHour && hour <= endHour;
    }
}
