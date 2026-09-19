package com.example.demo.storage;

import com.example.demo.common.ApiException;
import com.example.demo.annotation.BizOperationLog;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OssService {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectStorageProvider provider;

    public List<Map<String, Object>> page(String keyword) {
        String like = keyword == null ? "%%" : "%" + keyword + "%";
        return jdbcTemplate.queryForList("select * from sys_oss where is_deleted = 0 and (oss_code like ? or remark like ?) order by id desc", like, like);
    }

    public Map<String, Object> detail(Long id) {
        return jdbcTemplate.queryForMap("select * from sys_oss where id = ? and is_deleted = 0", id);
    }

    @Transactional
    @BizOperationLog(bizType = "oss", bizName = "对象存储配置", operationType = "保存", bizId = "#id")
    public long save(Map<String, Object> input, Long id) {
        Object status = input.getOrDefault("status", 0);
        if (Integer.valueOf(1).equals(Integer.valueOf(String.valueOf(status)))) {
            jdbcTemplate.update("update sys_oss set status=0, update_time=current_timestamp where is_deleted=0 and id<>?", id == null ? -1 : id);
        }
        if (id == null) {
            jdbcTemplate.update("insert into sys_oss (oss_code, endpoint, outside_endpoint, remark, status, is_deleted, create_time, update_time) values (?, ?, ?, ?, ?, 0, current_timestamp, current_timestamp)",
                    input.get("ossCode"), input.get("endpoint"), input.get("outsideEndpoint"), input.get("remark"), status);
            return jdbcTemplate.queryForObject("select last_insert_id()", Long.class);
        }
        jdbcTemplate.update("update sys_oss set oss_code=?, endpoint=?, outside_endpoint=?, remark=?, status=?, update_time=current_timestamp where id=? and is_deleted=0",
                input.get("ossCode"), input.get("endpoint"), input.get("outsideEndpoint"), input.get("remark"), status, id);
        return id;
    }

    @Transactional
    public void enable(Long id) {
        jdbcTemplate.update("update sys_oss set status=0, update_time=current_timestamp where is_deleted=0 and id<>?", id);
        jdbcTemplate.update("update sys_oss set status=1, update_time=current_timestamp where id=? and is_deleted=0", id);
    }

    public void remove(Long id) {
        jdbcTemplate.update("update sys_oss set is_deleted=1, status=0, update_time=current_timestamp where id=?", id);
    }

    @Transactional
    @BizOperationLog(bizType = "attachment", bizName = "附件", operationType = "上传")
    public Map<String, Object> upload(org.springframework.web.multipart.MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException("文件不能为空");
        }
        String key = provider.upload(new UploadObject(fileResource(file), file.getOriginalFilename(), file.getContentType(), file.getSize()));
        String url = provider.getAccessUrl(key);
        try {
            jdbcTemplate.update("insert into sys_attach (object_key, url, file_name, extension, content_type, file_size, is_deleted, create_time, update_time) values (?, ?, ?, ?, ?, ?, 0, current_timestamp, current_timestamp)",
                    key, url, file.getOriginalFilename(), extension(file.getOriginalFilename()), file.getContentType(), file.getSize());
        } catch (RuntimeException ex) {
            provider.delete(key);
            throw ex;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("objectKey", key);
        result.put("url", url);
        result.put("fileName", file.getOriginalFilename());
        result.put("size", file.getSize());
        return result;
    }

    private java.io.InputStream fileResource(org.springframework.web.multipart.MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (java.io.IOException ex) {
            throw new ApiException("无法读取上传文件");
        }
    }

    private String extension(String name) {
        if (name == null) return null;
        int dot = name.lastIndexOf('.');
        return dot < 0 ? null : name.substring(dot + 1).toLowerCase(java.util.Locale.ROOT);
    }
}
