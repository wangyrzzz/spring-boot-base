package com.example.demo.controller;

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
    @GetMapping("/detail") public Result<?> detail(@RequestParam Long id) { return Result.ok(service.detail(id)); }
    @GetMapping({"/list", "/page"}) public Result<?> list(@RequestParam(required = false) String paramKey) { return Result.ok(service.list(paramKey)); }
    @GetMapping("/value") public Result<?> value(@RequestParam String paramKey) { return Result.ok(service.value(paramKey)); }
    @PostMapping("/submit") public Result<Long> submit(@RequestBody Map<String,Object> in) { return Result.ok(service.save(in)); }
    @DeleteMapping("/remove") public Result<Void> remove(@RequestParam Long id) { service.remove(id); return Result.ok(); }
}
