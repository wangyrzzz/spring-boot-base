package com.example.demo.sesrvice;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.common.ApiException;
import com.example.demo.common.AuthenticatedUser;
import com.example.demo.common.ClientCredentialService;
import com.example.demo.common.ClientPolicy;
import com.example.demo.common.JwtTokenService;
import com.example.demo.common.TokenPair;
import com.example.demo.entity.SysDept;
import com.example.demo.entity.SysRole;
import com.example.demo.entity.SysUser;
import com.example.demo.system.DeptService;
import com.example.demo.system.RoleService;
import com.example.demo.system.UserDeptService;
import com.example.demo.system.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokenService;
    private final ClientCredentialService clientCredentialService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final UserDeptService userDeptService;
    private final DeptService deptService;

    public TokenPair login(String username, String password, String clientCode) {
        ClientPolicy policy = clientCredentialService.requireGrant(clientCode, "password");
        SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null || !Integer.valueOf(1).equals(user.getStatus()) || !matchesPassword(password, user)) {
            throw new ApiException(401, "用户名或密码错误");
        }
        return tokenService.issue(toAuthUser(user, clientCode), clientCode,
                policy.accessTokenValidity(), policy.refreshTokenValidity());
    }

    private boolean matchesPassword(String rawPassword, SysUser user) {
        if (!StringUtils.hasText(rawPassword) || !StringUtils.hasText(user.getPassword())) {
            return false;
        }
        try {
            return passwordEncoder.matches(rawPassword, user.getPassword());
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    private AuthenticatedUser toAuthUser(SysUser user, String clientCode) {
        List<Long> roleIds = userRoleService.roleIds(user.getId());
        java.util.Map<Long, SysRole> roleById = roleService.listByIds(roleIds).stream()
                .collect(java.util.stream.Collectors.toMap(SysRole::getId, item -> item));
        List<RoleRow> roles = roleIds.stream().map(roleById::get).filter(java.util.Objects::nonNull)
                .map(role -> new RoleRow(role.getId(), role.getRoleCode(), role.getRoleName())).toList();
        Long deptId = userDeptService.firstDeptId(user.getId());
        SysDept dept = deptId == null ? null : deptService.getById(deptId);
        String fullDeptId = dept == null || !StringUtils.hasText(dept.getAncestors())
                ? (deptId == null ? null : String.valueOf(deptId)) : dept.getAncestors();
        return AuthenticatedUser.builder()
                .userId(user.getId())
                .clientCode(clientCode)
                .account(user.getUsername())
                .userName(user.getUsername())
                .nickName(user.getRealName())
                .deptId(deptId)
                .fullDeptId(fullDeptId)
                .roleName(roles.stream().map(RoleRow::name).collect(Collectors.joining(",")))
                .roleCodes(roles.stream().map(RoleRow::code).filter(StringUtils::hasText).toList())
                .roleIds(roles.stream().map(RoleRow::id).toList())
                .build();
    }

    private record RoleRow(Long id, String code, String name) {
    }
}
