package com.example.demo.common;

public final class AuthUserContext {
    private static final ThreadLocal<AuthenticatedUser> HOLDER = new ThreadLocal<>();

    private AuthUserContext() {
    }

    public static void set(AuthenticatedUser user) {
        HOLDER.set(user);
    }

    public static AuthenticatedUser get() {
        return HOLDER.get();
    }

    public static AuthenticatedUser required() {
        AuthenticatedUser user = get();
        if (user == null) {
            throw new ApiException(401, "未登录");
        }
        return user;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
