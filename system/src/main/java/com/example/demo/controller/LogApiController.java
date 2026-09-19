package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.annotation.PreAuth;
import com.example.demo.common.PageResult;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.Result;
import com.example.demo.entity.SysLogApi;
import com.example.demo.system.LogApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/retail-system/log-api")
@RequiredArgsConstructor
@PreAuth(RbacPermissionCodes.API_LOG_READ)
public class LogApiController {
    private final LogApiService service;

    @GetMapping("/detail")
    public Result<SysLogApi> detail(@RequestParam Long id) {
        return Result.ok(service.getById(id));
    }

    @GetMapping("/page")
    public PageResult<?> page(@RequestParam(required = false) String requestUri,
                              @RequestParam(required = false) String method,
                              @RequestParam(required = false) String requestIp,
                              @RequestParam(required = false) Integer httpStatus,
                              @RequestParam(defaultValue = "1") long page,
                              @RequestParam(defaultValue = "10") long limit) {
        Page<SysLogApi> result = service.page(page, limit, requestUri, method, requestIp, httpStatus);
        return PageResult.ok(result.getRecords(), result.getTotal());
    }
}
