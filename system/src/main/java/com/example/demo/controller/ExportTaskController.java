package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.annotation.PreAuth;
import com.example.demo.common.PageResult;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.Result;
import com.example.demo.entity.SysExportTask;
import com.example.demo.system.ExportTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/retail-system/export-task")
@RequiredArgsConstructor
@PreAuth(RbacPermissionCodes.EXPORT_TASK_READ)
public class ExportTaskController {
    private final ExportTaskService service;

    @GetMapping("/detail")
    public Result<SysExportTask> detail(@RequestParam Long id) {
        return Result.ok(service.getById(id));
    }

    @GetMapping("/page")
    public PageResult<?> page(@RequestParam(required = false) Integer status,
                              @RequestParam(required = false) Integer exportBizType,
                              @RequestParam(required = false) Long createBy,
                              @RequestParam(defaultValue = "1") long page,
                              @RequestParam(defaultValue = "10") long limit) {
        Page<SysExportTask> result = service.page(page, limit, status, exportBizType, createBy);
        return PageResult.ok(result.getRecords(), result.getTotal());
    }
}
