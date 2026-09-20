package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.annotation.PreAuth;
import com.example.demo.common.PageResult;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.Result;
import com.example.demo.entity.SysDept;
import com.example.demo.system.DeptService;
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
@RequestMapping("/retail-system/dept")
@RequiredArgsConstructor
@PreAuth(RbacPermissionCodes.RBAC_MANAGE)
public class DeptController {
    private final DeptService service;

    @GetMapping("/detail")
    public Result<SysDept> detail(@RequestParam Long id) {
        return Result.ok(service.getById(id));
    }

    @GetMapping("/page")
    public PageResult<?> page(@RequestParam(required = false) String name,
                              @RequestParam(required = false) String code,
                              @RequestParam(required = false) Long parentId,
                              @RequestParam(defaultValue = "1") long page,
                              @RequestParam(defaultValue = "10") long limit) {
        Page<SysDept> result = service.page(page, limit, name, code, parentId);
        return PageResult.ok(result.getRecords(), result.getTotal());
    }

    @GetMapping("/select")
    public Result<List<SysDept>> select(@RequestParam(required = false) Long parentId) {
        return Result.ok(service.select(parentId));
    }

    @PostMapping("/submit")
    public Result<Long> submit(@RequestBody SysDept dept) {
        return Result.ok(service.saveOrUpdateDept(dept));
    }

    @DeleteMapping("/remove")
    public Result<Void> remove(@RequestParam Long id) {
        service.removeDept(id);
        return Result.ok();
    }
}
