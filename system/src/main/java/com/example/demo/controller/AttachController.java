package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.annotation.PreAuth;
import com.example.demo.common.PageResult;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.Result;
import com.example.demo.entity.SysAttach;
import com.example.demo.system.AttachService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/retail-resource/attach")
@RequiredArgsConstructor
@PreAuth(RbacPermissionCodes.ATTACH_READ)
public class AttachController {
    private final AttachService service;

    @GetMapping("/detail")
    public Result<SysAttach> detail(@RequestParam Long id) {
        return Result.ok(service.getById(id));
    }

    @GetMapping("/page")
    public PageResult<?> page(@RequestParam(required = false) String keyword,
                              @RequestParam(defaultValue = "1") long page,
                              @RequestParam(defaultValue = "10") long limit) {
        Page<SysAttach> result = service.page(page, limit, keyword);
        return PageResult.ok(result.getRecords(), result.getTotal());
    }

    @PostMapping("/submit")
    @PreAuth(RbacPermissionCodes.ATTACH_WRITE)
    public Result<Long> submit(@RequestBody SysAttach attach) {
        return Result.ok(service.saveOrUpdateAttach(attach));
    }

    @DeleteMapping("/remove")
    @PreAuth(RbacPermissionCodes.ATTACH_WRITE)
    public Result<Void> remove(@RequestParam Long id) {
        service.removeById(id);
        return Result.ok();
    }
}
