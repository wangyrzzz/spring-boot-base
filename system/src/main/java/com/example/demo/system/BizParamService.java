package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.common.ApiException;
import com.example.demo.entity.SysBizParam;
import com.example.demo.mapper.SysBizParamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BizParamService extends ServiceImpl<SysBizParamMapper, SysBizParam> {
    private static final String CACHE_PREFIX = "biz-param:";

    private final ObjectProvider<StringRedisTemplate> redisProvider;

    public List<SysBizParam> list(String paramKey) {
        LambdaQueryWrapper<SysBizParam> wrapper = new LambdaQueryWrapper<SysBizParam>()
                .like(StringUtils.hasText(paramKey), SysBizParam::getParamKey, paramKey)
                .orderByDesc(SysBizParam::getUpdateTime);
        return list(wrapper);
    }

    public Page<SysBizParam> page(long current, long size, String paramKey) {
        LambdaQueryWrapper<SysBizParam> wrapper = new LambdaQueryWrapper<SysBizParam>()
                .like(StringUtils.hasText(paramKey), SysBizParam::getParamKey, paramKey)
                .orderByDesc(SysBizParam::getUpdateTime);
        return page(new Page<>(current, size), wrapper);
    }

    public String value(String paramKey) {
        if (!StringUtils.hasText(paramKey)) {
            return null;
        }
        String cacheKey = cacheKey(paramKey);
        StringRedisTemplate redis = redisProvider.getIfAvailable();
        if (redis != null) {
            try {
                String cached = redis.opsForValue().get(cacheKey);
                if (cached != null) {
                    return cached;
                }
            } catch (RuntimeException ignored) {
            }
        }
        SysBizParam param = getOne(new LambdaQueryWrapper<SysBizParam>()
                .eq(SysBizParam::getParamKey, paramKey)
                .eq(SysBizParam::getStatus, 1));
        if (param == null) {
            return null;
        }
        if (redis != null) {
            try {
                redis.opsForValue().set(cacheKey, param.getParamValue());
            } catch (RuntimeException ignored) {
            }
        }
        return param.getParamValue();
    }

    @Transactional
    public Long saveOrUpdateParam(SysBizParam param) {
        if (param == null || !StringUtils.hasText(param.getParamKey())) {
            throw new ApiException(400, "参数键不能为空");
        }
        SysBizParam old = param.getId() == null ? null : getById(param.getId());
        assertUnique(param);
        if (param.getStatus() == null) {
            param.setStatus(1);
        }
        saveOrUpdate(param);
        if (old != null && !old.getParamKey().equals(param.getParamKey())) {
            clear(old.getParamKey());
        }
        clear(param.getParamKey());
        return param.getId();
    }

    @Transactional
    public void removeParam(Long id) {
        SysBizParam old = getById(id);
        if (old != null) {
            removeById(id);
            clear(old.getParamKey());
        }
    }

    private void assertUnique(SysBizParam param) {
        Long count = lambdaQuery()
                .eq(SysBizParam::getParamKey, param.getParamKey())
                .ne(param.getId() != null, SysBizParam::getId, param.getId())
                .count();
        if (count > 0) {
            throw new ApiException(400, "业务参数键已存在");
        }
    }

    private String cacheKey(String paramKey) {
        return CACHE_PREFIX + paramKey;
    }

    private void clear(String paramKey) {
        StringRedisTemplate redis = redisProvider.getIfAvailable();
        if (StringUtils.hasText(paramKey) && redis != null) {
            try {
                redis.delete(cacheKey(paramKey));
            } catch (RuntimeException ignored) {
            }
        }
    }
}
