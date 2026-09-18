package com.example.demo.storage;

import com.example.demo.common.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalObjectStorageProvider implements ObjectStorageProvider {
    private final Path root;
    private final String publicBaseUrl;

    @Autowired
    public LocalObjectStorageProvider(
            @Value("${sys.oss.local-root:${java.io.tmpdir}/spring-boot-base-objects}") String root,
            @Value("${sys.oss.public-base-url:}") String publicBaseUrl) {
        this(Path.of(root), publicBaseUrl);
    }

    public LocalObjectStorageProvider(Path root) {
        this(root, "");
    }

    public LocalObjectStorageProvider(Path root, String publicBaseUrl) {
        this.root = root.toAbsolutePath().normalize();
        this.publicBaseUrl = publicBaseUrl == null ? "" : publicBaseUrl.replaceAll("/+$", "");
    }

    @Override
    public String upload(UploadObject object) {
        if (object == null || object.content() == null || object.size() <= 0) {
            throw new ApiException("文件不能为空");
        }
        String extension = extension(object.originalFilename());
        String key = java.time.LocalDate.now() + "/" + UUID.randomUUID() + extension;
        Path target = safePath(key);
        try (var input = object.content()) {
            Files.createDirectories(target.getParent());
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
            return key;
        } catch (IOException ex) {
            throw new ApiException("文件上传失败");
        }
    }

    @Override
    public void delete(String objectKey) {
        if (!StringUtils.hasText(objectKey)) {
            return;
        }
        try {
            Files.deleteIfExists(safePath(objectKey));
        } catch (IOException ex) {
            throw new ApiException("文件删除失败");
        }
    }

    @Override
    public String getAccessUrl(String objectKey) {
        if (!StringUtils.hasText(objectKey)) {
            return null;
        }
        String prefix = StringUtils.hasText(publicBaseUrl) ? publicBaseUrl : "";
        return prefix + "/retail-resource/oss/file/" + objectKey;
    }

    public Path resolve(String objectKey) {
        return safePath(objectKey);
    }

    private Path safePath(String objectKey) {
        if (!StringUtils.hasText(objectKey) || objectKey.contains("\\") || objectKey.contains("..")) {
            throw new ApiException("非法文件路径");
        }
        Path target = root.resolve(objectKey).normalize();
        if (!target.startsWith(root)) {
            throw new ApiException("非法文件路径");
        }
        return target;
    }

    private String extension(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "";
        }
        String name = Path.of(fileName).getFileName().toString();
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            return "";
        }
        String ext = name.substring(dot).toLowerCase(java.util.Locale.ROOT);
        return ext.matches("\\.[a-z0-9]{1,16}") ? ext : "";
    }
}
