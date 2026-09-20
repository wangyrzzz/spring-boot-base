package com.example.demo.common;

import com.example.demo.dto.RbacGrantRequest;
import com.example.demo.dto.RbacRoleRequest;
import com.example.demo.dto.RbacUserRoleRequest;
import com.example.demo.entity.SysMenu;
import com.example.demo.entity.SysRole;
import com.example.demo.mapper.SysRoleMapper;
import com.example.demo.sesrvice.IUserService;
import com.example.demo.system.MenuService;
import com.example.demo.system.RoleMenuService;
import com.example.demo.system.RoleService;
import com.example.demo.system.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Administrative operations for the RBAC model. */
@Service
@RequiredArgsConstructor
public class RbacAdminService {
    private final IUserService userService;
    private final RoleService roleService;
    private final MenuService menuService;
    private final UserRoleService userRoleService;
    private final RoleMenuService roleMenuService;

    public List<RbacRoleView> listRoles() {
        List<SysRole> roles = roleService.list();
        Map<Long, List<Long>> permissionIds = roleMenuService.list().stream()
                .collect(Collectors.groupingBy(item -> item.getRoleId(), Collectors.mapping(item -> item.getMenuId(), Collectors.toList())));
        return roles.stream().map(role -> new RbacRoleView(role.getId(), role.getRoleCode(), role.getRoleName(),
                role.getRemark(), permissionIds.getOrDefault(role.getId(), List.of()).stream().distinct().toList())).toList();
    }

    public List<RbacPermissionView> listPermissions() {
        return menuService.list().stream().map(menu -> new RbacPermissionView(menu.getId(), menu.getParentId(),
                menu.getMenuType(), menu.getName(), menu.getCode(), menu.getPath(), menu.getSort(), menu.getRemark())).toList();
    }

    public List<Long> roleIds(Long userId) {
        requireUser(userId);
        return userRoleService.roleIds(userId);
    }

    @Transactional
    public Long saveRole(RbacRoleRequest request) {
        SysRole role = new SysRole();
        role.setId(request.getId());
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setRemark(request.getRemark());
        Long roleId = roleService.saveOrUpdateRole(role);
        if (request.getPermissionIds() != null) {
            replaceRolePermissions(List.of(roleId), request.getPermissionIds());
        }
        return roleId;
    }

    @Transactional
    public void removeRole(Long roleId) {
        if (roleId == null) {
            throw new ApiException(400, "角色不能为空");
        }
        SysRole role = roleService.getById(roleId);
        if (role == null) {
            throw new ApiException(404, "角色不存在");
        }
        if (RoleConstant.ADMINISTRATOR.equalsIgnoreCase(role.getRoleCode())) {
            throw new ApiException(400, "不能删除管理员角色");
        }
        userRoleService.remove(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.example.demo.entity.SysUserRole>()
                .eq(com.example.demo.entity.SysUserRole::getRoleId, roleId));
        roleMenuService.remove(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.example.demo.entity.SysRoleMenu>()
                .eq(com.example.demo.entity.SysRoleMenu::getRoleId, roleId));
        roleService.removeById(roleId);
    }

    @Transactional
    public void grant(RbacGrantRequest request) {
        replaceRolePermissions(unique(request.getRoleIds()), request.getPermissionIds());
    }

    @Transactional
    public void grantUserRoles(RbacUserRoleRequest request) {
        requireUser(request.getUserId());
        List<Long> roleIds = unique(request.getRoleIds());
        if (!roleIds.isEmpty()) {
            requireActiveRoles(roleIds);
        }
        userRoleService.replace(request.getUserId(), roleIds);
    }

    private void replaceRolePermissions(List<Long> roleIds, List<Long> permissionIds) {
        requireActiveRoles(roleIds);
        List<Long> normalizedPermissionIds = unique(permissionIds);
        requireActivePermissions(normalizedPermissionIds);
        for (Long roleId : roleIds) {
            roleMenuService.replace(roleId, normalizedPermissionIds);
        }
    }

    private void requireUser(Long userId) {
        if (userId == null || userService.getById(userId) == null) {
            throw new ApiException(404, "用户不存在");
        }
    }

    private void requireActiveRoles(List<Long> roleIds) {
        if (roleIds.isEmpty()) {
            throw new ApiException(400, "角色不能为空");
        }
        long activeCount = roleService.listByIds(roleIds).size();
        if (activeCount != roleIds.size()) {
            throw new ApiException(404, "包含不存在或已删除的角色");
        }
    }

    private void requireActivePermissions(List<Long> permissionIds) {
        if (!permissionIds.isEmpty() && menuService.listByIds(permissionIds).size() != permissionIds.size()) {
            throw new ApiException(404, "包含不存在或已删除的权限");
        }
    }

    private List<Long> unique(Collection<Long> values) {
        if (values == null) {
            return new ArrayList<>();
        }
        return values.stream().filter(value -> value != null)
                .collect(Collectors.toCollection(LinkedHashSet::new)).stream().toList();
    }
}
