package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.common.ApiException;
import com.example.demo.entity.SysBizParam;
import com.example.demo.mapper.SysBizParamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BizParamService extends ServiceImpl<SysBizParamMapper, SysBizParam> {
    private static final String CACHE_PREFIX = "biz-param:";

    private final StringRedisTemplate redis;

    public List<SysBizParam> list(String paramKey, Integer bizModule) {
        LambdaQueryWrapper<SysBizParam> wrapper = new LambdaQueryWrapper<SysBizParam>()
                .eq(bizModule != null, SysBizParam::getBizModule, bizModule)
                .like(StringUtils.hasText(paramKey), SysBizParam::getParamKey, paramKey)
                .orderByDesc(SysBizParam::getUpdateTime);
        return list(wrapper);
    }

    public Page<SysBizParam> page(long current, long size, String paramKey, Integer bizModule) {
        LambdaQueryWrapper<SysBizParam> wrapper = new LambdaQueryWrapper<SysBizParam>()
                .eq(bizModule != null, SysBizParam::getBizModule, bizModule)
                .like(StringUtils.hasText(paramKey), SysBizParam::getParamKey, paramKey)
                .orderByDesc(SysBizParam::getUpdateTime);
        return page(new Page<>(current, size), wrapper);
    }

    public String value(Integer bizModule, String paramKey) {
        if (bizModule == null || !StringUtils.hasText(paramKey)) {
            return null;
        }
        String cacheKey = cacheKey(bizModule, paramKey);
        try {
            String cached = redis.opsForValue().get(cacheKey);
            if (cached != null) {
                return cached;
            }
        } catch (RuntimeException ignored) {
        }
        SysBizParam param = getOne(new LambdaQueryWrapper<SysBizParam>()
                .eq(SysBizParam::getBizModule, bizModule)
                .eq(SysBizParam::getParamKey, paramKey)
                .eq(SysBizParam::getStatus, 1));
        if (param == null) {
            return null;
        }
        try {
            redis.opsForValue().set(cacheKey, param.getParamValue());
        } catch (RuntimeException ignored) {
        }
        return param.getParamValue();
    }

    @Transactional
    public Long saveOrUpdateParam(SysBizParam param) {
        if (param == null || param.getBizModule() == null || !StringUtils.hasText(param.getParamKey())) {
            throw new ApiException(400, "业务模块和参数键不能为空");
        }
        SysBizParam old = param.getId() == null ? null : getById(param.getId());
        assertUnique(param);
        if (param.getStatus() == null) {
            param.setStatus(1);
        }
        saveOrUpdate(param);
        if (old != null && (old.getBizModule() != null && !old.getBizModule().equals(param.getBizModule())
                || !old.getParamKey().equals(param.getParamKey()))) {
            clear(old.getBizModule(), old.getParamKey());
        }
        clear(param.getBizModule(), param.getParamKey());
        return param.getId();
    }

    @Transactional
    public void removeParam(Long id) {
        SysBizParam old = getById(id);
        if (old != null) {
            removeById(id);
            clear(old.getBizModule(), old.getParamKey());
        }
    }

    private void assertUnique(SysBizParam param) {
        Long count = lambdaQuery()
                .eq(SysBizParam::getBizModule, param.getBizModule())
                .eq(SysBizParam::getParamKey, param.getParamKey())
                .ne(param.getId() != null, SysBizParam::getId, param.getId())
                .count();
        if (count > 0) {
            throw new ApiException(400, "业务参数键已存在");
        }
    }

    private String cacheKey(Integer bizModule, String paramKey) {
        return CACHE_PREFIX + bizModule + ":" + paramKey;
    }

    private void clear(Integer bizModule, String paramKey) {
        if (bizModule != null && StringUtils.hasText(paramKey)) {
            try {
                redis.delete(cacheKey(bizModule, paramKey));
            } catch (RuntimeException ignored) {
            }
        }
    }
}
