package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.annotation.PreAuth;
import com.example.demo.common.PageResult;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.Result;
import com.example.demo.entity.SysNotice;
import com.example.demo.system.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/retail-system/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService service;

    @GetMapping("/detail")
    @PreAuth(RbacPermissionCodes.NOTICE_READ)
    public Result<SysNotice> detail(@RequestParam Long id) {
        return Result.ok(service.getById(id));
    }

    @GetMapping("/page")
    @PreAuth(RbacPermissionCodes.NOTICE_READ)
    public PageResult<?> page(@RequestParam(required = false) String title,
                              @RequestParam(required = false) Integer type,
                              @RequestParam(required = false) Integer status,
                              @RequestParam(defaultValue = "1") long page,
                              @RequestParam(defaultValue = "10") long limit) {
        Page<SysNotice> result = service.page(page, limit, title, type, status);
        return PageResult.ok(result.getRecords(), result.getTotal());
    }

    @GetMapping("/published")
    @PreAuth(RbacPermissionCodes.NOTICE_READ)
    public PageResult<?> published(@RequestParam(defaultValue = "1") long page,
                                   @RequestParam(defaultValue = "10") long limit) {
        Page<SysNotice> result = service.published(page, limit);
        return PageResult.ok(result.getRecords(), result.getTotal());
    }

    @PostMapping("/submit")
    @PreAuth(RbacPermissionCodes.NOTICE_WRITE)
    public Result<Long> save(@RequestBody SysNotice notice) {
        service.saveOrUpdate(notice);
        return Result.ok(notice.getId());
    }

    @DeleteMapping("/remove")
    @PreAuth(RbacPermissionCodes.NOTICE_WRITE)
    public Result<Void> remove(@RequestParam Long id) {
        service.removeById(id);
        return Result.ok();
    }
}
