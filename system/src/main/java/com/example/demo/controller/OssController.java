package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.storage.LocalObjectStorageProvider;
import com.example.demo.storage.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/retail-resource/oss")
@RequiredArgsConstructor
public class OssController {
    private final OssService service;
    private final LocalObjectStorageProvider localProvider;

    @GetMapping("/detail") public Result<Map<String, Object>> detail(@RequestParam Long id) { return Result.ok(service.detail(id)); }
    @GetMapping("/page") public Result<?> page(@RequestParam(required = false) String keyword) { return Result.ok(service.page(keyword)); }
    @PostMapping("/save") public Result<Long> save(@RequestBody Map<String, Object> input) { return Result.ok(service.save(input, null)); }
    @PutMapping("/update") public Result<Long> update(@RequestParam Long id, @RequestBody Map<String, Object> input) { return Result.ok(service.save(input, id)); }
    @PostMapping("/submit") public Result<Long> submit(@RequestBody Map<String, Object> input) { return Result.ok(service.save(input, input.get("id") == null ? null : Long.valueOf(String.valueOf(input.get("id"))))); }
    @DeleteMapping("/remove") public Result<Void> remove(@RequestParam Long id) { service.remove(id); return Result.ok(); }
    @PostMapping("/enable") public Result<Void> enable(@RequestParam Long id) { service.enable(id); return Result.ok(); }
    @PostMapping(value = "/endpoint/put-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> upload(@RequestPart("file") MultipartFile file) { return Result.ok(service.upload(file)); }

    @GetMapping("/file/**")
    public ResponseEntity<InputStreamResource> file(jakarta.servlet.http.HttpServletRequest request) throws java.io.IOException {
        String path = request.getRequestURI().substring(request.getRequestURI().indexOf("/file/") + 6);
        java.nio.file.Path target = localProvider.resolve(path);
        if (!java.nio.file.Files.exists(target)) return ResponseEntity.notFound().build();
        String contentType = java.nio.file.Files.probeContentType(target);
        return ResponseEntity.ok().contentType(contentType == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(contentType))
                .body(new InputStreamResource(java.nio.file.Files.newInputStream(target)));
    }
}
