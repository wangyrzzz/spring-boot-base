package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.annotation.PreAuth;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.RbacAdminService;
import com.example.demo.common.Result;
import com.example.demo.common.PageResult;
import com.example.demo.entity.SysRole;
import com.example.demo.system.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/retail-system/role")
@RequiredArgsConstructor
@PreAuth(RbacPermissionCodes.RBAC_MANAGE)
public class RoleController {
    private final RoleService service;
    private final RbacAdminService adminService;

    @GetMapping("/detail")
    public Result<SysRole> detail(@RequestParam Long id) {
        return Result.ok(service.getById(id));
    }

    @GetMapping("/page")
    public PageResult<?> page(@RequestParam(required = false) String roleCode,
                              @RequestParam(required = false) String roleName,
                              @RequestParam(defaultValue = "1") long page,
                              @RequestParam(defaultValue = "10") long limit) {
        Page<SysRole> result = service.page(page, limit, roleCode, roleName);
        return PageResult.ok(result.getRecords(), result.getTotal());
    }

    @PostMapping("/submit")
    public Result<Long> submit(@RequestBody SysRole role) {
        return Result.ok(service.saveOrUpdateRole(role));
    }

    @DeleteMapping("/remove")
    public Result<Void> remove(@RequestParam Long id) {
        adminService.removeRole(id);
        return Result.ok();
    }
}
