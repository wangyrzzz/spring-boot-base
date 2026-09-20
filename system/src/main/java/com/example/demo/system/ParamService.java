package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.annotation.BizOperationLog;
import com.example.demo.common.ApiException;
import com.example.demo.entity.SysParam;
import com.example.demo.enums.EnableStatusEnum;
import com.example.demo.mapper.SysParamMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParamService extends ServiceImpl<SysParamMapper, SysParam> {
    private static final String CACHE_PREFIX = "param:";

    private final ObjectProvider<StringRedisTemplate> redisProvider;
    private final ObjectMapper objectMapper;

    public List<SysParam> list(String key) {
        return lambdaQuery().like(StringUtils.hasText(key), SysParam::getParamKey, key)
                .orderByDesc(SysParam::getId).list();
    }

    public Page<SysParam> page(long current, long size, String key) {
        LambdaQueryWrapper<SysParam> wrapper = new LambdaQueryWrapper<SysParam>()
                .like(StringUtils.hasText(key), SysParam::getParamKey, key)
                .orderByDesc(SysParam::getId);
        return page(new Page<>(current, size), wrapper);
    }

    public SysParam detail(Long id) {
        return getById(id);
    }

    public String value(String key) {
        StringRedisTemplate redis = redisProvider.getIfAvailable();
        if (redis != null) {
            try {
                String cached = redis.opsForValue().get(cacheKey(key));
                if (cached != null) {
                    return cached;
                }
            } catch (RuntimeException ignored) {
            }
        }
        SysParam param = lambdaQuery().eq(SysParam::getParamKey, key)
                .eq(SysParam::getStatus, EnableStatusEnum.ENABLED.getCode()).one();
        String value = param == null ? null : param.getParamValue();
        if (value != null && redis != null) {
            try {
                redis.opsForValue().set(cacheKey(key), value);
            } catch (RuntimeException ignored) {
            }
        }
        return value;
    }

    @Transactional
    @BizOperationLog(bizType = "param", bizName = "系统参数", operationType = "保存", bizId = "#in['id']")
    public long save(Map<String, Object> in) {
        SysParam param = objectMapper.convertValue(in, SysParam.class);
        if (!StringUtils.hasText(param.getParamKey())) {
            throw new ApiException(400, "参数键不能为空");
        }
        SysParam old = param.getId() == null ? null : getById(param.getId());
        long duplicate = lambdaQuery().eq(SysParam::getParamKey, param.getParamKey())
                .ne(param.getId() != null, SysParam::getId, param.getId()).count();
        if (duplicate > 0) {
            throw new ApiException(400, "参数键已存在");
        }
        if (param.getStatus() == null) {
            param.setStatus(EnableStatusEnum.ENABLED.getCode());
        }
        saveOrUpdate(param);
        if (old != null) {
            clear(old.getParamKey());
        }
        clear(param.getParamKey());
        return param.getId();
    }

    @BizOperationLog(bizType = "param", bizName = "系统参数", operationType = "删除", bizId = "#id")
    public void remove(Long id) {
        SysParam old = getById(id);
        if (old != null) {
            removeById(id);
            clear(old.getParamKey());
        }
    }

    private String cacheKey(String key) {
        return CACHE_PREFIX + key;
    }

    private void clear(String key) {
        StringRedisTemplate redis = redisProvider.getIfAvailable();
        if (StringUtils.hasText(key) && redis != null) {
            try {
                redis.delete(cacheKey(key));
            } catch (RuntimeException ignored) {
            }
        }
    }
}
