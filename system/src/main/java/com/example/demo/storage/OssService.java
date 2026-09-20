package com.example.demo.storage;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.annotation.BizOperationLog;
import com.example.demo.common.ApiException;
import com.example.demo.entity.SysAttach;
import com.example.demo.entity.SysOss;
import com.example.demo.enums.EnableStatusEnum;
import com.example.demo.mapper.SysOssMapper;
import com.example.demo.system.AttachService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OssService extends ServiceImpl<SysOssMapper, SysOss> {
    private final ObjectStorageProvider provider;
    private final AttachService attachService;
    private final ObjectMapper objectMapper;

    public List<SysOss> page(String keyword) {
        String value = StringUtils.hasText(keyword) ? keyword : null;
        return lambdaQuery().like(value != null, SysOss::getOssCode, value)
                .or(value != null).like(value != null, SysOss::getRemark, value)
                .orderByDesc(SysOss::getId).list().stream().map(this::sanitize).toList();
    }

    public SysOss detail(Long id) {
        SysOss oss = getById(id);
        return oss == null ? null : sanitize(oss);
    }

    @Transactional
    @BizOperationLog(bizType = "oss", bizName = "对象存储配置", operationType = "保存", bizId = "#id")
    public long save(Map<String, Object> input, Long id) {
        SysOss oss = objectMapper.convertValue(input, SysOss.class);
        oss.setId(id);
        if (oss.getStatus() == null) {
            oss.setStatus(EnableStatusEnum.ENABLED.getCode());
        }
        if (oss.getProviderType() == null) {
            oss.setProviderType("local");
        }
        if (!StringUtils.hasText(oss.getOssCode())) {
            throw new ApiException(400, "资源编号不能为空");
        }
        if (EnableStatusEnum.ENABLED.getCode() == oss.getStatus()) {
            disableOthers(id);
        }
        saveOrUpdate(oss);
        return oss.getId();
    }

    @Transactional
    public void enable(Long id) {
        disableOthers(id);
        lambdaUpdate().eq(SysOss::getId, id).set(SysOss::getStatus, EnableStatusEnum.ENABLED.getCode()).update();
    }

    @Transactional
    public void remove(Long id) {
        lambdaUpdate().eq(SysOss::getId, id).set(SysOss::getStatus, EnableStatusEnum.DISABLED.getCode()).update();
        removeById(id);
    }

    @Transactional
    @BizOperationLog(bizType = "attachment", bizName = "附件", operationType = "上传")
    public Map<String, Object> upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException("文件不能为空");
        }
        String key = provider.upload(new UploadObject(fileResource(file), file.getOriginalFilename(), file.getContentType(), file.getSize()));
        String url = provider.getAccessUrl(key);
        try {
            SysAttach attach = new SysAttach();
            attach.setObjectKey(key);
            attach.setUrl(url);
            attach.setFileName(file.getOriginalFilename());
            attach.setExtension(extension(file.getOriginalFilename()));
            attach.setContentType(file.getContentType());
            attach.setFileSize(file.getSize());
            attachService.saveOrUpdateAttach(attach);
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

    private void disableOthers(Long id) {
        LambdaUpdateWrapper<SysOss> wrapper = new LambdaUpdateWrapper<SysOss>()
                .eq(SysOss::getStatus, EnableStatusEnum.ENABLED.getCode())
                .ne(id != null, SysOss::getId, id);
        SysOss disabled = new SysOss();
        disabled.setStatus(EnableStatusEnum.DISABLED.getCode());
        update(disabled, wrapper);
    }

    private SysOss sanitize(SysOss oss) {
        oss.setAccessKey(null);
        oss.setSecretKey(null);
        return oss;
    }

    private java.io.InputStream fileResource(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (java.io.IOException ex) {
            throw new ApiException("无法读取文件");
        }
    }

    private String extension(String name) {
        if (name == null) {
            return null;
        }
        int dot = name.lastIndexOf('.');
        return dot < 0 ? null : name.substring(dot + 1).toLowerCase(java.util.Locale.ROOT);
    }
}
