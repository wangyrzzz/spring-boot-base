package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.system.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/retail-resource/document")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService service;
    @GetMapping("/detail") public Result<?> detail(@RequestParam Long id) { return Result.ok(service.detail(id)); }
    @GetMapping("/latest") public Result<?> latest(@RequestParam String type) { return Result.ok(service.latest(type)); }
    @GetMapping("/page") public Result<?> page(@RequestParam(required = false) String type, @RequestParam(required = false) String keyword) { return Result.ok(service.page(type, keyword)); }
    @GetMapping("/select") public Result<?> select(@RequestParam(required = false) String type) { return Result.ok(service.select(type)); }
    @PostMapping("/save") public Result<Long> save(@RequestBody Map<String,Object> input) { return Result.ok(service.save(input, false)); }
    @PutMapping("/update") public Result<Long> update(@RequestBody Map<String,Object> input) { return Result.ok(service.save(input, true)); }
    @PostMapping("/remove") public Result<Void> remove(@RequestBody List<Long> ids) { service.remove(ids); return Result.ok(); }
    @DeleteMapping("/remove") public Result<Void> removeOne(@RequestParam Long id) { service.remove(List.of(id)); return Result.ok(); }
}
