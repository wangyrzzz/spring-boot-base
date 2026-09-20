package com.example.demo.system;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DictionaryService {
    private static final String SYSTEM_CACHE_PREFIX = "dict:";
    private static final String BUSINESS_CACHE_PREFIX = "dict-biz:";

    private final DictService dictService;
    private final DictBizService dictBizService;
    private final ObjectProvider<StringRedisTemplate> redisProvider;
    private final ObjectMapper objectMapper;

    public List<?> list(boolean business, String code, Long parentId) {
        String cacheKey = cacheKey(business, code, parentId);
        StringRedisTemplate redis = redisProvider.getIfAvailable();
        if (redis != null) {
            try {
                String cached = redis.opsForValue().get(cacheKey);
                if (cached != null) {
                    Class<?> type = business ? com.example.demo.entity.SysDictBiz.class : com.example.demo.entity.SysDict.class;
                    JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, type);
                    return objectMapper.readValue(cached, listType);
                }
            } catch (Exception ignored) {
            }
        }
        List<?> result = business ? dictBizService.list(code, parentId) : dictService.list(code, parentId);
        if (redis != null) {
            try {
                redis.opsForValue().set(cacheKey, objectMapper.writeValueAsString(result));
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    public Object detail(boolean business, Long id) {
        return business ? dictBizService.getById(id) : dictService.getById(id);
    }

    public long save(boolean business, Map<String, Object> input) {
        if (business) {
            com.example.demo.entity.SysDictBiz dict = objectMapper.convertValue(input, com.example.demo.entity.SysDictBiz.class);
            Long id = dictBizService.saveOrUpdateDict(dict);
            clear(business);
            return id;
        }
        com.example.demo.entity.SysDict dict = objectMapper.convertValue(input, com.example.demo.entity.SysDict.class);
        Long id = dictService.saveOrUpdateDict(dict);
        clear(business);
        return id;
    }

    public void remove(boolean business, Long id) {
        if (business) {
            dictBizService.removeById(id);
        } else {
            dictService.removeById(id);
        }
        clear(business);
    }

    private String cacheKey(boolean business, String code, Long parentId) {
        String prefix = business ? BUSINESS_CACHE_PREFIX : SYSTEM_CACHE_PREFIX;
        return prefix + String.valueOf(code) + ":" + String.valueOf(parentId);
    }

    private void clear(boolean business) {
        StringRedisTemplate redis = redisProvider.getIfAvailable();
        if (redis == null) {
            return;
        }
        try {
            String pattern = (business ? BUSINESS_CACHE_PREFIX : SYSTEM_CACHE_PREFIX) + "*";
            java.util.Set<String> keys = redis.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redis.delete(keys);
            }
        } catch (RuntimeException ignored) {
        }
    }
}
