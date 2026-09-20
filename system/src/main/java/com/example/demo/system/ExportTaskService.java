package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.entity.SysExportTask;
import com.example.demo.mapper.SysExportTaskMapper;
import org.springframework.stereotype.Service;

@Service
public class ExportTaskService extends ServiceImpl<SysExportTaskMapper, SysExportTask> {
    public Page<SysExportTask> page(long current, long size, Integer status, Integer exportBizType, Long createBy) {
        LambdaQueryWrapper<SysExportTask> wrapper = new LambdaQueryWrapper<SysExportTask>()
                .eq(status != null, SysExportTask::getStatus, status)
                .eq(exportBizType != null, SysExportTask::getExportBizType, exportBizType)
                .eq(createBy != null, SysExportTask::getCreateBy, createBy)
                .orderByDesc(SysExportTask::getCreateTime).orderByDesc(SysExportTask::getId);
        return page(new Page<>(current, size), wrapper);
    }
}
