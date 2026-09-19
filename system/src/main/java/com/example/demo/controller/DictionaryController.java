package com.example.demo.controller;

import com.example.demo.annotation.PreAuth;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.Result;
import com.example.demo.system.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DictionaryController {
    private final DictionaryService service;

    @GetMapping({"/retail-system/dict/{action}", "/retail-system/dict-biz/{action}"})
    @PreAuth(RbacPermissionCodes.DICT_READ)
    public Result<?> get(@PathVariable String action, @RequestParam(required = false) String code,
                         @RequestParam(required = false) Long parentId, @RequestParam(required = false) Long id,
                         jakarta.servlet.http.HttpServletRequest request) {
        boolean business = request.getRequestURI().contains("dict-biz");
        if ("detail".equals(action)) return Result.ok(service.detail(business, id));
        return Result.ok(service.list(business, code, parentId));
    }

    @PostMapping({"/retail-system/dict/submit", "/retail-system/dict-biz/submit"})
    @PreAuth(RbacPermissionCodes.DICT_WRITE)
    public Result<Long> submit(@RequestBody Map<String, Object> input, jakarta.servlet.http.HttpServletRequest request) {
        return Result.ok(service.save(request.getRequestURI().contains("dict-biz"), input));
    }

    @DeleteMapping({"/retail-system/dict/remove", "/retail-system/dict-biz/remove"})
    @PreAuth(RbacPermissionCodes.DICT_WRITE)
    public Result<Void> remove(@RequestParam Long id, jakarta.servlet.http.HttpServletRequest request) {
        service.remove(request.getRequestURI().contains("dict-biz"), id); return Result.ok();
    }

    @GetMapping({"/retail-system/dict/dictionary", "/retail-system/dict/dictionary-tree",
            "/retail-system/dict/select", "/retail-system/dict/select-all",
            "/retail-system/dict-biz/dictionary", "/retail-system/dict-biz/dictionary-tree",
            "/retail-system/dict-biz/select", "/retail-system/dict-biz/select-all"})
    @PreAuth(RbacPermissionCodes.DICT_READ)
    public Result<?> select(@RequestParam(required = false) String code,
                            jakarta.servlet.http.HttpServletRequest request) {
        return Result.ok(service.list(request.getRequestURI().contains("dict-biz"), code, null));
    }
}
