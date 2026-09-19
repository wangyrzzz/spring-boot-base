package com.example.demo.common;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Resolves role and menu permissions from the system RBAC tables. */
@Service
@RequiredArgsConstructor
public class RbacPermissionService {
    private final JdbcTemplate jdbcTemplate;
    private final RbacProperties properties;

    public boolean permissionAll() {
        AuthenticatedUser user = AuthUserContext.get();
        return user != null && properties.isAdministratorBypass() && isAdministrator(user);
    }

    public boolean hasPermission(String permission) {
        AuthenticatedUser user = AuthUserContext.get();
        if (user == null || !StringUtils.hasText(permission)) {
            return false;
        }
        if (permissionAll()) {
            return true;
        }
        List<Long> roleIds = user.getRoleIds();
        if (roleIds == null || roleIds.isEmpty()) {
            return false;
        }

        String holders = String.join(",", roleIds.stream().map(item -> "?").toList());
        List<Object> args = new ArrayList<>();
        args.add(permission);
        args.addAll(roleIds);
        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from sys_menu p "
                        + "inner join sys_role_menu rp on rp.menu_id = p.id "
                        + "where p.code = ? and coalesce(p.deleted, 0) = 0 and rp.role_id in (" + holders + ")",
                args.toArray(), Integer.class);
        return count != null && count > 0;
    }

    public boolean hasRole(String role) {
        AuthenticatedUser user = AuthUserContext.get();
        if (user == null || !StringUtils.hasText(role)) {
            return false;
        }
        String expected = role.trim().toLowerCase(Locale.ROOT);
        return roleValues(user).stream().anyMatch(actual -> actual.equals(expected));
    }

    public boolean hasAnyRole(String... roles) {
        if (roles == null) {
            return false;
        }
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAllRole(String... roles) {
        if (roles == null || roles.length == 0) {
            return true;
        }
        for (String role : roles) {
            if (!hasRole(role)) {
                return false;
            }
        }
        return true;
    }

    public RbacUserPermissionView currentUserPermissions() {
        AuthenticatedUser user = AuthUserContext.get();
        if (user == null) {
            return new RbacUserPermissionView(List.of(), List.of());
        }
        List<String> roleCodes = user.getRoleCodes() == null ? List.of() : user.getRoleCodes();
        if (permissionAll()) {
            return new RbacUserPermissionView(roleCodes, allPermissions());
        }
        List<Long> roleIds = user.getRoleIds();
        if (roleIds == null || roleIds.isEmpty()) {
            return new RbacUserPermissionView(roleCodes, List.of());
        }
        String holders = String.join(",", roleIds.stream().map(item -> "?").toList());
        List<String> permissions = jdbcTemplate.query(
                "select distinct p.code from sys_menu p "
                        + "inner join sys_role_menu rp on rp.menu_id = p.id "
                        + "where coalesce(p.deleted, 0) = 0 and p.code is not null and rp.role_id in (" + holders + ") "
                        + "order by p.code",
                (rs, rowNum) -> rs.getString(1), roleIds.toArray());
        return new RbacUserPermissionView(roleCodes, permissions);
    }

    private boolean isAdministrator(AuthenticatedUser user) {
        return roleValues(user).stream().anyMatch(role ->
                RoleConstant.ADMINISTRATOR.equals(role) || "管理员".equals(role));
    }

    private List<String> roleValues(AuthenticatedUser user) {
        List<String> values = new ArrayList<>();
        if (user.getRoleCodes() != null) {
            user.getRoleCodes().stream().filter(StringUtils::hasText)
                    .map(value -> value.trim().toLowerCase(Locale.ROOT)).forEach(values::add);
        }
        if (StringUtils.hasText(user.getRoleName())) {
            for (String value : user.getRoleName().split("[,，]")) {
                if (StringUtils.hasText(value)) {
                    values.add(value.trim().toLowerCase(Locale.ROOT));
                }
            }
        }
        return values;
    }

    private List<String> allPermissions() {
        return jdbcTemplate.query(
                "select distinct code from sys_menu "
                        + "where coalesce(deleted, 0) = 0 and code is not null order by code",
                (rs, rowNum) -> rs.getString(1));
    }
}
