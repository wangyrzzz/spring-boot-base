package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.annotation.PreAuth;
import com.example.demo.common.PageResult;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.Result;
import com.example.demo.entity.SysBizParam;
import com.example.demo.system.BizParamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/retail-system/biz-param")
@RequiredArgsConstructor
public class BizParamController {
    private final BizParamService service;

    @GetMapping("/detail")
    @PreAuth(RbacPermissionCodes.BIZ_PARAM_READ)
    public Result<SysBizParam> detail(@RequestParam Long id) {
        return Result.ok(service.getById(id));
    }

    @GetMapping("/page")
    @PreAuth(RbacPermissionCodes.BIZ_PARAM_READ)
    public PageResult<?> page(@RequestParam(required = false) String paramKey,
                              @RequestParam(required = false) Integer bizModule,
                              @RequestParam(defaultValue = "1") long page,
                              @RequestParam(defaultValue = "10") long limit) {
        Page<SysBizParam> result = service.page(page, limit, paramKey, bizModule);
        return PageResult.ok(result.getRecords(), result.getTotal());
    }

    @GetMapping("/value")
    @PreAuth(RbacPermissionCodes.BIZ_PARAM_READ)
    public Result<String> value(@RequestParam Integer bizModule, @RequestParam String paramKey) {
        return Result.ok(service.value(bizModule, paramKey));
    }

    @PostMapping("/submit")
    @PreAuth(RbacPermissionCodes.BIZ_PARAM_WRITE)
    public Result<Long> save(@RequestBody SysBizParam param) {
        return Result.ok(service.saveOrUpdateParam(param));
    }

    @DeleteMapping("/remove")
    @PreAuth(RbacPermissionCodes.BIZ_PARAM_WRITE)
    public Result<Void> remove(@RequestParam Long id) {
        service.removeParam(id);
        return Result.ok();
    }
}
