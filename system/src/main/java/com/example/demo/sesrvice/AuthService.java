package com.example.demo.sesrvice;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.common.ApiException;
import com.example.demo.common.AuthenticatedUser;
import com.example.demo.common.ClientCredentialService;
import com.example.demo.common.ClientPolicy;
import com.example.demo.common.JwtTokenService;
import com.example.demo.common.TokenPair;
import com.example.demo.entity.SysUser;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final IUserService userService;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokenService;
    private final ClientCredentialService clientCredentialService;

    public TokenPair login(String username, String password) {
        return login(username, password, null);
    }

    public TokenPair login(String username, String password, String clientId) {
        ClientPolicy policy = clientCredentialService.requireGrant(clientId, "password");
        SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null || !Integer.valueOf(1).equals(user.getStatus()) || !matchesAndMigrate(password, user)) {
            throw new ApiException(401, "用户名或密码错误");
        }
        return tokenService.issue(toAuthUser(user, clientId), clientId,
                policy.accessTokenValidity(), policy.refreshTokenValidity());
    }

    public TokenPair clientCredentials(String clientId, String clientSecret) {
        ClientPolicy policy = clientCredentialService.requireGrant(clientId, "client_credentials");
        clientCredentialService.verifySecret(policy, clientSecret);
        AuthenticatedUser machine = AuthenticatedUser.builder().userId(0L).clientId(clientId)
                .account(clientId).userName(clientId).nickName(clientId).build();
        return tokenService.issue(machine, clientId, policy.accessTokenValidity(), policy.refreshTokenValidity());
    }

    private boolean matchesAndMigrate(String rawPassword, SysUser user) {
        if (!StringUtils.hasText(rawPassword) || !StringUtils.hasText(user.getPassword())) {
            return false;
        }
        if (user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$")
                || user.getPassword().startsWith("$2y$")) {
            try {
                return passwordEncoder.matches(rawPassword, user.getPassword());
            } catch (IllegalArgumentException ignored) {
                return false;
            }
        }
        if (!rawPassword.equals(user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(rawPassword));
        userService.updateById(user);
        return true;
    }

    private AuthenticatedUser toAuthUser(SysUser user, String clientId) {
        List<RoleRow> roles = jdbcTemplate.query(
                "select r.id, r.role_code, r.role_name from sys_role r inner join sys_user_role ur on ur.role_id = r.id "
                        + "where ur.user_id = ? and r.deleted = 0 order by r.id",
                (rs, rowNum) -> new RoleRow(rs.getLong("id"), rs.getString("role_code"), rs.getString("role_name")), user.getId());
        Long deptId = jdbcTemplate.query(
                "select dept_id from sys_user_dept where user_id = ? order by id limit 1",
                (rs, rowNum) -> rs.getLong("dept_id"), user.getId()).stream().findFirst().orElse(null);
        String fullDeptId = deptId == null ? null : jdbcTemplate.query(
                "select ancestors from sys_dept where id = ? and deleted = 0",
                (rs, rowNum) -> rs.getString(1), deptId).stream().findFirst().orElse(String.valueOf(deptId));
        return AuthenticatedUser.builder()
                .userId(user.getId())
                .clientId(clientId)
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
