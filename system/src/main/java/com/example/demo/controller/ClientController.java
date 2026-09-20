package com.example.demo.controller;

import com.example.demo.annotation.PreAuth;
import com.example.demo.common.ClientCredentialService;
import com.example.demo.common.RbacPermissionCodes;
import com.example.demo.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/retail-system/client")
@RequiredArgsConstructor
@PreAuth(RbacPermissionCodes.CLIENT_MANAGE)
public class ClientController {
    private final ClientCredentialService service;
    @GetMapping("/page") public Result<?> list() { return Result.ok(service.listClients()); }
    @GetMapping("/detail") public Result<?> detail(@RequestParam Long id) { return Result.ok(service.detail(id)); }
    @PostMapping("/submit") public Result<Long> save(@RequestBody Map<String,Object> input) { return Result.ok(service.save(input)); }
    @PutMapping("/update") public Result<Long> update(@RequestBody Map<String,Object> input) { return Result.ok(service.save(input)); }
    @DeleteMapping("/remove") public Result<Void> remove(@RequestParam Long id) { service.remove(id); return Result.ok(); }
}
