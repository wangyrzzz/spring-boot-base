package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.annotation.BizOperationLog;
import com.example.demo.common.ApiException;
import com.example.demo.entity.SysDocument;
import com.example.demo.enums.DeletedFlagEnum;
import com.example.demo.mapper.SysDocumentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentService extends ServiceImpl<SysDocumentMapper, SysDocument> {
    private static final DateTimeFormatter DELETE_SUFFIX = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final int DOCUMENT_CODE_MAX_LENGTH = 128;

    private final ObjectMapper objectMapper;

    public SysDocument detail(Long id) {
        return getById(id);
    }

    public List<SysDocument> page(Integer type, String keyword) {
        LambdaQueryWrapper<SysDocument> wrapper = new LambdaQueryWrapper<SysDocument>()
                .eq(type != null, SysDocument::getType, type)
                .and(StringUtils.hasText(keyword), item -> item.like(SysDocument::getCode, keyword)
                        .or().like(SysDocument::getTitle, keyword))
                .orderByAsc(SysDocument::getSort).orderByAsc(SysDocument::getId);
        return list(wrapper);
    }

    public List<SysDocument> select(Integer type) {
        return lambdaQuery().eq(type != null, SysDocument::getType, type)
                .orderByAsc(SysDocument::getSort).orderByAsc(SysDocument::getId).list();
    }

    public SysDocument latest(Integer type) {
        return lambdaQuery().eq(SysDocument::getType, type)
                .orderByDesc(SysDocument::getSort).orderByDesc(SysDocument::getId).last("limit 1").one();
    }

    @Transactional
    @BizOperationLog(bizType = "document", bizName = "文档", operationType = "保存", bizId = "#input['id']")
    public long save(Map<String, Object> input, boolean update) {
        SysDocument document = objectMapper.convertValue(input, SysDocument.class);
        if (!StringUtils.hasText(document.getCode())) {
            throw new ApiException(400, "文档编码不能为空");
        }
        if (document.getId() != null && !update) {
            throw new ApiException(400, "新增文档不能携带 ID");
        }
        if (document.getSort() == null) {
            document.setSort(0);
        }
        long duplicate = lambdaQuery().eq(SysDocument::getCode, document.getCode())
                .ne(document.getId() != null, SysDocument::getId, document.getId()).count();
        if (duplicate > 0) {
            throw new ApiException(400, "文档编码已存在");
        }
        saveOrUpdate(document);
        return document.getId();
    }

    @Transactional
    @BizOperationLog(bizType = "document", bizName = "文档", operationType = "删除", bizId = "#ids")
    public void remove(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        List<SysDocument> documents = listByIds(ids);
        for (SysDocument document : documents) {
            String released = releasedCode(document.getCode(), document.getId());
            long sameCode = lambdaQuery().eq(SysDocument::getCode, released).count();
            while (sameCode > 0) {
                released = releasedCode(document.getCode(), document.getId() + System.nanoTime());
                sameCode = lambdaQuery().eq(SysDocument::getCode, released).count();
            }
            SysDocument update = new SysDocument();
            update.setCode(released);
            update.setDeleted(DeletedFlagEnum.DELETED.getCode());
            update(update, new LambdaUpdateWrapper<SysDocument>().eq(SysDocument::getId, document.getId()));
        }
    }

    public static String releasedCode(String code, long id) {
        String suffix = "_delete_" + LocalDateTime.now().format(DELETE_SUFFIX) + "_" + Math.abs(id % 100000);
        String safeCode = code == null ? "document" : code;
        int baseLength = Math.max(0, DOCUMENT_CODE_MAX_LENGTH - suffix.length());
        String base = safeCode.substring(0, Math.min(safeCode.length(), baseLength));
        return (base + suffix).substring(0, Math.min(DOCUMENT_CODE_MAX_LENGTH, base.length() + suffix.length()));
    }
}
