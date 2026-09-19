package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.PageResult;
import com.example.demo.common.Result;
import com.example.demo.entity.SysRegion;
import com.example.demo.entity.SysRegionNode;
import com.example.demo.system.RegionService;
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
@RequestMapping("/retail-system/region")
@RequiredArgsConstructor
public class RegionController {
    private final RegionService service;

    @GetMapping("/detail")
    public Result<SysRegion> detail(@RequestParam Long id) {
        return Result.ok(service.getById(id));
    }

    @GetMapping("/page")
    public PageResult<?> page(@RequestParam(required = false) String code,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) String parentCode,
                              @RequestParam(required = false) Integer status,
                              @RequestParam(defaultValue = "1") long page,
                              @RequestParam(defaultValue = "10") long limit) {
        Page<SysRegion> result = service.page(page, limit, code, name, parentCode, status);
        return PageResult.ok(result.getRecords(), result.getTotal());
    }

    @GetMapping("/lazy-list")
    public Result<List<SysRegion>> lazyList(@RequestParam(required = false) String parentCode,
                                            @RequestParam(required = false) String code,
                                            @RequestParam(required = false) String name) {
        return Result.ok(service.lazyList(parentCode, code, name));
    }

    @GetMapping("/lazy-tree")
    public Result<List<SysRegionNode>> lazyTree(@RequestParam(required = false) String parentCode,
                                                @RequestParam(required = false) String code,
                                                @RequestParam(required = false) String name) {
        return Result.ok(service.lazyTree(parentCode, code, name));
    }

    @GetMapping("/select")
    public Result<List<SysRegion>> select(@RequestParam(required = false) String parentCode) {
        return Result.ok(service.select(parentCode));
    }

    @PostMapping("/submit")
    public Result<Long> save(@RequestBody SysRegion region) {
        return Result.ok(service.saveOrUpdateRegion(region));
    }

    @DeleteMapping("/remove")
    public Result<Void> remove(@RequestParam Long id) {
        service.removeRegion(id);
        return Result.ok();
    }
}
