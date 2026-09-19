package com.example.demo.common;

import com.example.demo.dto.RbacGrantRequest;
import com.example.demo.dto.RbacRoleRequest;
import com.example.demo.dto.RbacUserRoleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/** Administrative operations for the four-table RBAC model. */
@Service
@RequiredArgsConstructor
public class RbacAdminService {
    private final JdbcTemplate jdbcTemplate;

    public List<RbacRoleView> listRoles() {
        return jdbcTemplate.query(
                "select id, role_code, role_name, remark from sys_role where coalesce(deleted, 0) = 0 order by id",
                (rs, rowNum) -> new RbacRoleView(rs.getLong("id"), rs.getString("role_code"),
                        rs.getString("role_name"), rs.getString("remark"), permissionIds(rs.getLong("id"))));
    }

    public List<RbacPermissionView> listPermissions() {
        return jdbcTemplate.query(
                "select id, parent_id, menu_type, name, code, perm_path, sort, remark "
                        + "from sys_manage_permission where coalesce(deleted, 0) = 0 "
                        + "order by coalesce(parent_id, 0), sort, id",
                (rs, rowNum) -> new RbacPermissionView(rs.getLong("id"), getLong(rs, "parent_id"),
                        rs.getObject("menu_type", Integer.class), rs.getString("name"), rs.getString("code"),
                        rs.getString("perm_path"), rs.getObject("sort", Integer.class), rs.getString("remark")));
    }

    public List<Long> roleIds(Long userId) {
        requireUser(userId);
        return jdbcTemplate.query("select role_id from sys_user_role where user_id = ? order by id",
                (rs, rowNum) -> rs.getLong(1), userId);
    }

    @Transactional
    public Long saveRole(RbacRoleRequest request) {
        String roleCode = request.getRoleCode().trim();
        String roleName = request.getRoleName().trim();
        Integer duplicate = jdbcTemplate.queryForObject(
                "select count(1) from sys_role where role_code = ? and coalesce(deleted, 0) = 0 "
                        + "and (? is null or id <> ?)", Integer.class, roleCode, request.getId(), request.getId());
        if (duplicate != null && duplicate > 0) {
            throw new ApiException(400, "角色编码已存在");
        }

        Long roleId = request.getId();
        if (roleId == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(
                        "insert into sys_role (role_code, role_name, remark, deleted) values (?, ?, ?, 0)",
                        Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, roleCode);
                statement.setString(2, roleName);
                statement.setString(3, request.getRemark());
                return statement;
            }, keyHolder);
            roleId = keyHolder.getKey().longValue();
        } else {
            requireActiveRole(roleId);
            jdbcTemplate.update("update sys_role set role_code = ?, role_name = ?, remark = ? where id = ?",
                    roleCode, roleName, request.getRemark(), roleId);
        }
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
        String roleCode = jdbcTemplate.query("select role_code from sys_role where id = ? and deleted = 0",
                (rs, rowNum) -> rs.getString(1), roleId).stream().findFirst()
                .orElseThrow(() -> new ApiException(404, "角色不存在"));
        if (RoleConstant.ADMINISTRATOR.equalsIgnoreCase(roleCode)) {
            throw new ApiException(400, "不能删除管理员角色");
        }
        jdbcTemplate.update("delete from sys_user_role where role_id = ?", roleId);
        jdbcTemplate.update("delete from sys_role_manage_permission where role_id = ?", roleId);
        jdbcTemplate.update("update sys_role set deleted = 1 where id = ?", roleId);
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
        jdbcTemplate.update("delete from sys_user_role where user_id = ?", request.getUserId());
        if (!roleIds.isEmpty()) {
            jdbcTemplate.batchUpdate("insert into sys_user_role (user_id, role_id) values (?, ?)",
                    roleIds, roleIds.size(), (statement, roleId) -> {
                        statement.setLong(1, request.getUserId());
                        statement.setLong(2, roleId);
                    });
        }
    }

    private void replaceRolePermissions(List<Long> roleIds, List<Long> permissionIds) {
        requireActiveRoles(roleIds);
        List<Long> normalizedPermissionIds = unique(permissionIds);
        requireActivePermissions(normalizedPermissionIds);
        for (Long roleId : roleIds) {
            jdbcTemplate.update("delete from sys_role_manage_permission where role_id = ?", roleId);
            if (!normalizedPermissionIds.isEmpty()) {
                jdbcTemplate.batchUpdate("insert into sys_role_manage_permission (role_id, manage_permission_id) values (?, ?)",
                        normalizedPermissionIds, normalizedPermissionIds.size(), (statement, permissionId) -> {
                            statement.setLong(1, roleId);
                            statement.setLong(2, permissionId);
                        });
            }
        }
    }

    private List<Long> permissionIds(Long roleId) {
        return jdbcTemplate.query("select manage_permission_id from sys_role_manage_permission where role_id = ? order by id",
                (rs, rowNum) -> rs.getLong(1), roleId).stream().distinct().toList();
    }

    private void requireUser(Long userId) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from sys_user where id = ? and coalesce(deleted, 0) = 0", Integer.class, userId);
        if (count == null || count == 0) {
            throw new ApiException(404, "用户不存在");
        }
    }

    private void requireActiveRole(Long roleId) {
        requireActiveRoles(List.of(roleId));
    }

    private void requireActiveRoles(List<Long> roleIds) {
        if (roleIds.isEmpty()) {
            throw new ApiException(400, "角色不能为空");
        }
        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from sys_role where id in (" + holders(roleIds.size()) + ") "
                        + "and coalesce(deleted, 0) = 0", Integer.class, roleIds.toArray());
        if (count == null || count != roleIds.size()) {
            throw new ApiException(404, "包含不存在或已删除的角色");
        }
    }

    private void requireActivePermissions(List<Long> permissionIds) {
        if (permissionIds.isEmpty()) {
            return;
        }
        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from sys_manage_permission where id in (" + holders(permissionIds.size()) + ") "
                        + "and coalesce(deleted, 0) = 0", Integer.class, permissionIds.toArray());
        if (count == null || count != permissionIds.size()) {
            throw new ApiException(404, "包含不存在或已删除的权限");
        }
    }

    private List<Long> unique(Collection<Long> values) {
        if (values == null) {
            return new ArrayList<>();
        }
        return values.stream().filter(value -> value != null).collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new))
                .stream().toList();
    }

    private String holders(int size) {
        return String.join(",", java.util.Collections.nCopies(size, "?"));
    }

    private Long getLong(java.sql.ResultSet resultSet, String column) throws java.sql.SQLException {
        Object value = resultSet.getObject(column);
        return value == null ? null : ((Number) value).longValue();
    }
}
