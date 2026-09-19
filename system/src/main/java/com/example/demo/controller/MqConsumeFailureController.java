package com.example.demo.controller;

import com.example.demo.annotation.PreAuth;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.PageResult;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.Result;
import com.example.demo.dto.MqBatchRequest;
import com.example.demo.dto.MqResolveRequest;
import com.example.demo.mq.core.MqAdminService;
import com.example.demo.mq.core.MqConsumeFailureService;
import com.example.demo.mq.model.MqConsumeFailure;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/retail-system/mq-consume-failure")
@RequiredArgsConstructor
@PreAuth(RbacPermissionCodes.MQ_MANAGE)
public class MqConsumeFailureController {

    private final MqConsumeFailureService consumeFailureService;
    private final MqAdminService adminService;

    @GetMapping("/detail")
    public Result<MqConsumeFailure> detail(@RequestParam Long id) {
        return Result.ok(consumeFailureService.getById(id));
    }

    @GetMapping("/page")
    public PageResult<?> page(@RequestParam(required = false) Integer status,
                              @RequestParam(required = false) String destination,
                              @RequestParam(required = false) String consumerName,
                              @RequestParam(defaultValue = "1") long page,
                              @RequestParam(defaultValue = "10") long limit) {
        Page<MqConsumeFailure> result = consumeFailureService.page(page, limit, status, destination, consumerName);
        return PageResult.ok(result.getRecords(), result.getTotal());
    }

    @PostMapping("/retry")
    public Result<Void> retry(@RequestParam Long id) {
        adminService.retryConsume(id);
        return Result.ok();
    }

    @PostMapping("/retry-batch")
    public Result<Void> retryBatch(@RequestBody MqBatchRequest request) {
        adminService.retryConsumeBatch(request.ids());
        return Result.ok();
    }

    @PostMapping("/resolve")
    public Result<Void> resolve(@RequestParam Long id, @RequestBody(required = false) MqResolveRequest request) {
        adminService.resolveConsume(id, request == null ? null : request.remark());
        return Result.ok();
    }
}
