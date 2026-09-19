package com.example.demo.common;

/** Common SpEL expressions for {@link com.example.demo.annotation.PreAuth}. */
public final class AuthConstant {
    public static final String PERMIT_ALL = "permitAll()";
    public static final String DENY_ALL = "denyAll()";
    public static final String PERMISSION_ALL = "permissionAll()";
    public static final String HAS_AUTH = "hasAuth()";
    public static final String HAS_ROLE_ADMINISTRATOR = "hasRole('administrator')";
    public static final String HAS_ROLE_ADMIN = "hasAnyRole('administrator', 'admin')";
    public static final String HAS_ROLE_USER = "hasRole('user')";

    private AuthConstant() {
    }
}
