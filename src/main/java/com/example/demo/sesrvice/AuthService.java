package com.example.demo.sesrvice;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.common.ApiException;
import com.example.demo.common.AuthenticatedUser;
import com.example.demo.common.JwtTokenService;
import com.example.demo.common.TokenPair;
import com.example.demo.entity.User;
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

    public TokenPair login(String username, String password) {
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null || !Integer.valueOf(1).equals(user.getStatus()) || !matchesAndMigrate(password, user)) {
            throw new ApiException(401, "用户名或密码错误");
        }
        return tokenService.issue(toAuthUser(user));
    }

    private boolean matchesAndMigrate(String rawPassword, User user) {
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

    private AuthenticatedUser toAuthUser(User user) {
        List<RoleRow> roles = jdbcTemplate.query(
                "select r.id, r.role_name from sys_role r inner join sys_user_role ur on ur.role_id = r.id "
                        + "where ur.user_id = ? and r.deleted = 0 order by r.id",
                (rs, rowNum) -> new RoleRow(rs.getLong("id"), rs.getString("role_name")), user.getId());
        Long deptId = user.getDeptId();
        String fullDeptId = deptId == null ? null : jdbcTemplate.query(
                "select ancestors from sys_dept where id = ? and is_deleted = 0",
                (rs, rowNum) -> rs.getString(1), deptId).stream().findFirst().orElse(String.valueOf(deptId));
        return AuthenticatedUser.builder()
                .userId(user.getId())
                .account(user.getUsername())
                .userName(user.getUsername())
                .nickName(user.getRealName())
                .deptId(deptId)
                .fullDeptId(fullDeptId)
                .roleName(roles.stream().map(RoleRow::name).collect(Collectors.joining(",")))
                .roleIds(roles.stream().map(RoleRow::id).toList())
                .build();
    }

    private record RoleRow(Long id, String name) {
    }
}
