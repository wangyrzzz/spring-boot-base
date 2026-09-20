package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.common.ApiException;
import com.example.demo.entity.SysAttach;
import com.example.demo.mapper.SysAttachMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AttachService extends ServiceImpl<SysAttachMapper, SysAttach> {
    public Page<SysAttach> page(long current, long size, String keyword) {
        LambdaQueryWrapper<SysAttach> wrapper = new LambdaQueryWrapper<SysAttach>()
                .like(StringUtils.hasText(keyword), SysAttach::getFileName, keyword)
                .or(StringUtils.hasText(keyword))
                .like(StringUtils.hasText(keyword), SysAttach::getObjectKey, keyword)
                .orderByDesc(SysAttach::getCreateTime).orderByDesc(SysAttach::getId);
        return page(new Page<>(current, size), wrapper);
    }

    @Transactional
    public Long saveOrUpdateAttach(SysAttach attach) {
        if (attach == null || !StringUtils.hasText(attach.getObjectKey())
                || !StringUtils.hasText(attach.getUrl()) || !StringUtils.hasText(attach.getFileName())) {
            throw new ApiException(400, "附件对象键、地址和文件名不能为空");
        }
        if (attach.getFileSize() == null) {
            attach.setFileSize(0L);
        }
        saveOrUpdate(attach);
        return attach.getId();
    }
}
