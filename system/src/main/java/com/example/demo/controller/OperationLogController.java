package com.example.demo.controller;

import com.example.demo.annotation.PreAuth;
import com.example.demo.common.AuthUserContext;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.Result;
import com.example.demo.system.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/retail-system/bizLog")
@RequiredArgsConstructor
@PreAuth(RbacPermissionCodes.BIZ_LOG_READ)
public class OperationLogController {
    private final OperationLogService service;

    @GetMapping("/page") public Result<?> page(@RequestParam(required = false) String bizType, @RequestParam(required = false) String bizId) { return Result.ok(service.page(bizType, bizId)); }
    @GetMapping("/detail") public Result<?> detail(@RequestParam Long id) { return Result.ok(service.detail(id)); }
    @GetMapping("/bizHistory") public Result<?> bizHistory(@RequestParam String bizType, @RequestParam String bizId) { return Result.ok(service.page(bizType, bizId)); }
    @GetMapping("/bizTypes") public Result<?> bizTypes() { return Result.ok(service.types()); }
    @GetMapping("/statistics") public Result<?> statistics() { return Result.ok(service.statistics()); }
    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<byte[]> export(@RequestParam(required = false) String bizType, @RequestParam(required = false) String bizId) {
        List<Map<String,Object>> rows = service.page(bizType, bizId);
        StringBuilder csv = new StringBuilder("id,biz_type,biz_id,operation_type,operator_name,success,create_time\n");
        for (Map<String,Object> row : rows) csv.append(row.get("id")).append(',').append(row.get("bizType")).append(',').append(row.get("bizId")).append(',').append(row.get("operationType")).append(',').append(row.get("operatorName")).append(',').append(row.get("success")).append(',').append(row.get("createTime")).append('\n');
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=operation-log.csv").contentType(MediaType.parseMediaType("text/csv;charset=UTF-8")).body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }
}
