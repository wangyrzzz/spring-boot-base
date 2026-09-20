package com.example.demo.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.SysMenu;
import com.example.demo.entity.SysRoleMenu;
import com.example.demo.system.MenuService;
import com.example.demo.system.RoleMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Resolves role and menu permissions from the system RBAC tables. */
@Service
@RequiredArgsConstructor
public class RbacPermissionService {
    private final RbacProperties properties;
    private final MenuService menuService;
    private final RoleMenuService roleMenuService;

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
        List<Long> menuIds = roleMenuService.list(new LambdaQueryWrapper<SysRoleMenu>()
                        .in(SysRoleMenu::getRoleId, roleIds)).stream()
                .map(SysRoleMenu::getMenuId).distinct().toList();
        if (menuIds.isEmpty()) {
            return false;
        }
        return menuService.lambdaQuery().in(SysMenu::getId, menuIds)
                .eq(SysMenu::getCode, permission).count() > 0;
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
        List<Long> menuIds = roleMenuService.list(new LambdaQueryWrapper<SysRoleMenu>()
                        .in(SysRoleMenu::getRoleId, roleIds)).stream()
                .map(SysRoleMenu::getMenuId).distinct().toList();
        List<String> permissions = menuIds.isEmpty() ? List.of() : menuService.listByIds(menuIds).stream()
                .map(SysMenu::getCode).filter(StringUtils::hasText).distinct().sorted().toList();
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
        return menuService.list().stream().map(SysMenu::getCode)
                .filter(StringUtils::hasText).distinct().sorted().toList();
    }
}
