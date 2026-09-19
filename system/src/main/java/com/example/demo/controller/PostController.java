package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.annotation.PreAuth;
import com.example.demo.common.PageResult;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.Result;
import com.example.demo.entity.SysPost;
import com.example.demo.system.PostService;
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
@RequestMapping("/retail-system/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService service;

    @GetMapping("/detail")
    @PreAuth(RbacPermissionCodes.POST_READ)
    public Result<SysPost> detail(@RequestParam Long id) {
        return Result.ok(service.getById(id));
    }

    @GetMapping("/page")
    @PreAuth(RbacPermissionCodes.POST_READ)
    public PageResult<?> page(@RequestParam(required = false) String postCode,
                              @RequestParam(required = false) String postName,
                              @RequestParam(required = false) Integer status,
                              @RequestParam(defaultValue = "1") long page,
                              @RequestParam(defaultValue = "10") long limit) {
        Page<SysPost> result = service.page(page, limit, postCode, postName, status);
        return PageResult.ok(result.getRecords(), result.getTotal());
    }

    @GetMapping("/select")
    @PreAuth(RbacPermissionCodes.POST_READ)
    public Result<List<SysPost>> select() {
        return Result.ok(service.select());
    }

    @PostMapping("/submit")
    @PreAuth(RbacPermissionCodes.POST_WRITE)
    public Result<Long> save(@RequestBody SysPost post) {
        service.saveOrUpdate(post);
        return Result.ok(post.getId());
    }

    @DeleteMapping("/remove")
    @PreAuth(RbacPermissionCodes.POST_WRITE)
    public Result<Void> remove(@RequestParam Long id) {
        service.removeById(id);
        return Result.ok();
    }
}
