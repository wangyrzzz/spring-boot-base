package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.entity.SysLogApi;
import com.example.demo.mapper.SysLogApiMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class LogApiService extends ServiceImpl<SysLogApiMapper, SysLogApi> {
    public Page<SysLogApi> page(long current, long size, String requestUri, String method,
                                String requestIp, Integer httpStatus) {
        LambdaQueryWrapper<SysLogApi> wrapper = new LambdaQueryWrapper<SysLogApi>()
                .like(StringUtils.hasText(requestUri), SysLogApi::getRequestUri, requestUri)
                .eq(StringUtils.hasText(method), SysLogApi::getMethod, method)
                .like(StringUtils.hasText(requestIp), SysLogApi::getRequestIp, requestIp)
                .eq(httpStatus != null, SysLogApi::getHttpStatus, httpStatus)
                .orderByDesc(SysLogApi::getCreateTime)
                .orderByDesc(SysLogApi::getId);
        return page(new Page<>(current, size), wrapper);
    }

    @Transactional
    public void saveFromMessage(SysLogApi log) {
        if (log == null) {
            return;
        }
        log.setId(null);
        if (log.getCreateTime() == null) {
            log.setCreateTime(new Date());
        }
        if (log.getUpdateTime() == null) {
            log.setUpdateTime(log.getCreateTime());
        }
        if (log.getUpdateBy() == null) {
            log.setUpdateBy(log.getCreateBy());
        }
        if (log.getSuccess() == null) {
            log.setSuccess(log.getHttpStatus() != null && log.getHttpStatus() < 400 ? 1 : 0);
        }
        if (log.getDeleted() == null) {
            log.setDeleted(0);
        }
        getBaseMapper().insert(log);
    }
}
