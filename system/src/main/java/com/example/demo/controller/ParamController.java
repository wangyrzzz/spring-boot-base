package com.example.demo.controller;

import com.example.demo.annotation.PreAuth;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.Result;
import com.example.demo.system.ParamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/retail-system/param")
@RequiredArgsConstructor
public class ParamController {
    private final ParamService service;
    @GetMapping("/detail") @PreAuth(RbacPermissionCodes.PARAM_READ)
    public Result<?> detail(@RequestParam Long id) { return Result.ok(service.detail(id)); }
    @GetMapping("/page") @PreAuth(RbacPermissionCodes.PARAM_READ)
    public Result<?> list(@RequestParam(required = false) String paramKey) { return Result.ok(service.list(paramKey)); }
    @GetMapping("/value") @PreAuth(RbacPermissionCodes.PARAM_READ)
    public Result<?> value(@RequestParam String paramKey) { return Result.ok(service.value(paramKey)); }
    @PostMapping("/submit") @PreAuth(RbacPermissionCodes.PARAM_WRITE)
    public Result<Long> submit(@RequestBody Map<String,Object> in) { return Result.ok(service.save(in)); }
    @DeleteMapping("/remove") @PreAuth(RbacPermissionCodes.PARAM_WRITE)
    public Result<Void> remove(@RequestParam Long id) { service.remove(id); return Result.ok(); }
}
