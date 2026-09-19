package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.annotation.PreAuth;
import com.example.demo.common.AuthUserContext;
import com.example.demo.common.AuthenticatedUser;
import com.example.demo.common.PageResult;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.RbacPermissionService;
import com.example.demo.common.Result;
import com.example.demo.entity.BaseEntity;
import com.example.demo.entity.User;
import com.example.demo.query.UserQuery;
import com.example.demo.sesrvice.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户接口")
public class UserController extends BaseController {
    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RbacPermissionService rbacPermissionService;

    @GetMapping("/info")
    @Operation(summary = "获取当前用户")
    public Result<AuthenticatedUser> info() {
        return Result.ok(AuthUserContext.required());
    }

    @GetMapping("/permissions")
    public Result<?> permissions() {
        AuthUserContext.required();
        return Result.ok(rbacPermissionService.currentUserPermissions());
    }

    @PostMapping
    @PreAuth(RbacPermissionCodes.USER_WRITE)
    public Result<Boolean> insert(@RequestBody User user) {
        user.setStatus(1);
        if (StringUtils.isNotBlank(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.save(user);
        return Result.ok();
    }

    @PutMapping
    @PreAuth(RbacPermissionCodes.USER_WRITE)
    public Result<Boolean> update(@RequestBody User user) {
        if (StringUtils.isNotBlank(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.updateById(user);
        return Result.ok();
    }

    @GetMapping("/{id}")
    @PreAuth(RbacPermissionCodes.USER_READ)
    public Result<User> findById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @GetMapping("/page")
    @PreAuth(RbacPermissionCodes.USER_READ)
    public PageResult<List<User>> page(UserQuery userQuery) {
        Page<User> page = userService.page(userQuery);
        return PageResult.ok(page.getRecords(), page.getTotal());
    }

    @DeleteMapping("/{id}")
    @PreAuth(RbacPermissionCodes.USER_WRITE)
    public Result<Boolean> remove(@PathVariable Long id) {
        userService.removeById(id);
        return Result.ok();
    }

    @PatchMapping("/{id}")
    @PreAuth(RbacPermissionCodes.USER_WRITE)
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.update(new LambdaUpdateWrapper<User>().eq(BaseEntity::getId, id).set(User::getStatus, status));
        return Result.ok();
    }
}
