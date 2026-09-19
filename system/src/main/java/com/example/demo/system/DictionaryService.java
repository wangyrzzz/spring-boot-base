package com.example.demo.system;

import com.example.demo.common.ApiException;
import com.example.demo.annotation.BizOperationLog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DictionaryService {
    private final JdbcTemplate jdbcTemplate;
    private final StringRedisTemplate redis;

    public List<Map<String, Object>> list(boolean business, String code, Long parentId) {
        String table = table(business);
        String cacheKey = cachePrefix(business) + (code == null ? "*" : code) + ":" + (parentId == null ? "0" : parentId);
        try {
            String cached = redis.opsForValue().get(cacheKey);
            if (StringUtils.hasText(cached)) {
                return new com.fasterxml.jackson.databind.ObjectMapper().readValue(cached, List.class);
            }
        } catch (Exception ignored) {
            // Cache is an optimization; database remains authoritative.
        }
        StringBuilder sql = new StringBuilder("select * from ").append(table).append(" where deleted=0");
        java.util.List<Object> args = new java.util.ArrayList<>();
        if (StringUtils.hasText(code)) { sql.append(" and code=?"); args.add(code); }
        if (parentId != null) { sql.append(" and parent_id=?"); args.add(parentId); }
        sql.append(" order by sort asc, id asc");
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), args.toArray());
        try { redis.opsForValue().set(cacheKey, new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(result)); } catch (Exception ignored) { }
        return result;
    }

    public Map<String, Object> detail(boolean business, Long id) {
        return jdbcTemplate.queryForMap("select * from " + table(business) + " where id=? and deleted=0", id);
    }

    @Transactional
    @BizOperationLog(bizType = "dictionary", bizName = "字典", operationType = "保存", bizId = "#input['id']")
    public long save(boolean business, Map<String, Object> input) {
        String table = table(business);
        Long id = input.get("id") == null ? null : Long.valueOf(String.valueOf(input.get("id")));
        Object parentId = input.getOrDefault("parentId", 0);
        Object code = input.get("code");
        Object key = input.getOrDefault("dictKey", input.get("key"));
        Object value = input.getOrDefault("dictValue", input.get("value"));
        Object sort = input.getOrDefault("sort", 0);
        Object remark = input.get("remark");
        Object status = input.getOrDefault("status", 1);
        if (id == null) {
            jdbcTemplate.update("insert into " + table + " (parent_id, code, dict_key, dict_value, sort, remark, status, deleted, create_time, update_time) values (?,?,?,?,?,?,?,0,current_timestamp,current_timestamp)",
                    parentId, code, key, value, sort, remark, status);
            id = jdbcTemplate.queryForObject("select last_insert_id()", Long.class);
        } else {
            jdbcTemplate.update("update " + table + " set parent_id=?, code=?, dict_key=?, dict_value=?, sort=?, remark=?, status=?, update_time=current_timestamp where id=? and deleted=0",
                    parentId, code, key, value, sort, remark, status, id);
        }
        clearCache(business, code == null ? null : String.valueOf(code));
        return id;
    }

    @Transactional
    @BizOperationLog(bizType = "dictionary", bizName = "字典", operationType = "删除", bizId = "#id")
    public void remove(boolean business, Long id) {
        String table = table(business);
        Map<String, Object> row = detail(business, id);
        jdbcTemplate.update("update " + table + " set deleted=1, update_time=current_timestamp where id=?", id);
        clearCache(business, row.get("code") == null ? null : String.valueOf(row.get("code")));
    }

    private void clearCache(boolean business, String code) {
        try {
            String prefix = cachePrefix(business);
            java.util.Set<String> keys = redis.keys(prefix + "*");
            if (keys != null && !keys.isEmpty()) redis.delete(keys);
        } catch (RuntimeException ignored) { }
    }

    private String table(boolean business) { return business ? "sys_dict_biz" : "sys_dict"; }
    private String cachePrefix(boolean business) { return business ? "dict:biz:" : "dict:system:"; }
}
