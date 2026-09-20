package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.common.ApiException;
import com.example.demo.entity.SysScopeData;
import com.example.demo.enums.EnableStatusEnum;
import com.example.demo.mapper.SysScopeDataMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ScopeDataService extends ServiceImpl<SysScopeDataMapper, SysScopeData> {
    public Page<SysScopeData> page(long current, long size, String resourceCode, String scopeClass, Integer status) {
        LambdaQueryWrapper<SysScopeData> wrapper = new LambdaQueryWrapper<SysScopeData>()
                .like(StringUtils.hasText(resourceCode), SysScopeData::getResourceCode, resourceCode)
                .like(StringUtils.hasText(scopeClass), SysScopeData::getScopeClass, scopeClass)
                .eq(status != null, SysScopeData::getStatus, status)
                .orderByAsc(SysScopeData::getId);
        return page(new Page<>(current, size), wrapper);
    }

    @Transactional
    public Long saveOrUpdateScope(SysScopeData scope) {
        if (scope == null || !StringUtils.hasText(scope.getScopeClass())
                || !StringUtils.hasText(scope.getScopeColumn()) || scope.getScopeType() == null) {
            throw new ApiException(400, "数据权限 Mapper、字段和类型不能为空");
        }
        if (scope.getStatus() == null) {
            scope.setStatus(EnableStatusEnum.ENABLED.getCode());
        }
        saveOrUpdate(scope);
        return scope.getId();
    }
}
