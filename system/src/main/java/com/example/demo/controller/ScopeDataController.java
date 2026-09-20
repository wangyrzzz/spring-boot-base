package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.annotation.PreAuth;
import com.example.demo.common.PageResult;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.Result;
import com.example.demo.entity.SysScopeData;
import com.example.demo.system.ScopeDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/retail-system/scope-data")
@RequiredArgsConstructor
@PreAuth(RbacPermissionCodes.SCOPE_READ)
public class ScopeDataController {
    private final ScopeDataService service;

    @GetMapping("/detail")
    public Result<SysScopeData> detail(@RequestParam Long id) {
        return Result.ok(service.getById(id));
    }

    @GetMapping("/page")
    public PageResult<?> page(@RequestParam(required = false) String resourceCode,
                              @RequestParam(required = false) String scopeClass,
                              @RequestParam(required = false) Integer status,
                              @RequestParam(defaultValue = "1") long page,
                              @RequestParam(defaultValue = "10") long limit) {
        Page<SysScopeData> result = service.page(page, limit, resourceCode, scopeClass, status);
        return PageResult.ok(result.getRecords(), result.getTotal());
    }

    @PostMapping("/submit")
    @PreAuth(RbacPermissionCodes.SCOPE_WRITE)
    public Result<Long> submit(@RequestBody SysScopeData scope) {
        return Result.ok(service.saveOrUpdateScope(scope));
    }

    @DeleteMapping("/remove")
    @PreAuth(RbacPermissionCodes.SCOPE_WRITE)
    public Result<Void> remove(@RequestParam Long id) {
        service.removeById(id);
        return Result.ok();
    }
}
