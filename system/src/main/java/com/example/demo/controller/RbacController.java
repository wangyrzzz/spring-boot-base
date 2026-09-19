package com.example.demo.controller;

import com.example.demo.annotation.PreAuth;
import com.example.demo.common.RbacAdminService;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.RbacPermissionView;
import com.example.demo.common.RbacRoleView;
import com.example.demo.common.Result;
import com.example.demo.dto.RbacGrantRequest;
import com.example.demo.dto.RbacRoleRequest;
import com.example.demo.dto.RbacUserRoleRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/retail-system/rbac")
@RequiredArgsConstructor
@PreAuth(RbacPermissionCodes.RBAC_MANAGE)
public class RbacController {
    private final RbacAdminService service;

    @GetMapping("/roles")
    public Result<List<RbacRoleView>> roles() {
        return Result.ok(service.listRoles());
    }

    @PostMapping("/role/submit")
    public Result<Long> submitRole(@Valid @RequestBody RbacRoleRequest request) {
        return Result.ok(service.saveRole(request));
    }

    @DeleteMapping("/role/remove")
    public Result<Void> removeRole(@RequestParam Long id) {
        service.removeRole(id);
        return Result.ok();
    }

    @GetMapping("/permissions")
    public Result<List<RbacPermissionView>> permissions() {
        return Result.ok(service.listPermissions());
    }

    @PostMapping("/role/grant")
    public Result<Void> grant(@Valid @RequestBody RbacGrantRequest request) {
        service.grant(request);
        return Result.ok();
    }

    @GetMapping("/user/roles")
    public Result<List<Long>> userRoles(@RequestParam Long userId) {
        return Result.ok(service.roleIds(userId));
    }

    @PostMapping("/user/grant")
    public Result<Void> grantUserRoles(@Valid @RequestBody RbacUserRoleRequest request) {
        service.grantUserRoles(request);
        return Result.ok();
    }
}
