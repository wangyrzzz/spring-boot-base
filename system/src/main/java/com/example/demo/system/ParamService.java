package com.example.demo.system;

import lombok.RequiredArgsConstructor;
import com.example.demo.annotation.BizOperationLog;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParamService {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectProvider<StringRedisTemplate> redisProvider;

    public List<Map<String, Object>> list(String key) {
        if (key == null) return jdbcTemplate.queryForList("select * from sys_param where deleted=0 order by id desc");
        return jdbcTemplate.queryForList("select * from sys_param where deleted=0 and param_key like ? order by id desc", "%" + key + "%");
    }

    public Map<String, Object> detail(Long id) { return jdbcTemplate.queryForMap("select * from sys_param where id=? and deleted=0", id); }

    public String value(String key) {
        StringRedisTemplate redis = redisProvider.getIfAvailable();
        if (redis != null) {
            try {
                String cached = redis.opsForValue().get("param:" + key);
                if (cached != null) return cached;
            } catch (RuntimeException ignored) { }
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select param_value from sys_param where param_key=? and status=1 and deleted=0 limit 1", key);
        String value = rows.isEmpty() ? null : String.valueOf(rows.get(0).get("param_value"));
        if (value != null && redis != null) {
            try { redis.opsForValue().set("param:" + key, value); } catch (RuntimeException ignored) { }
        }
        return value;
    }

    @Transactional
    @BizOperationLog(bizType = "param", bizName = "系统参数", operationType = "保存", bizId = "#in['id']")
    public long save(Map<String, Object> in) {
        Long id = in.get("id") == null ? null : Long.valueOf(String.valueOf(in.get("id")));
        Object key = in.get("paramKey");
        if (id == null) {
            jdbcTemplate.update("insert into sys_param (param_name,param_key,param_value,remark,status,deleted,create_time,update_time) values (?,?,?,?,?,0,current_timestamp,current_timestamp)", in.get("paramName"), key, in.get("paramValue"), in.get("remark"), in.getOrDefault("status", 1));
            id = jdbcTemplate.queryForObject("select last_insert_id()", Long.class);
        } else {
            jdbcTemplate.update("update sys_param set param_name=?,param_key=?,param_value=?,remark=?,status=?,update_time=current_timestamp where id=? and deleted=0", in.get("paramName"), key, in.get("paramValue"), in.get("remark"), in.getOrDefault("status", 1), id);
        }
        clear(key); return id;
    }

    @BizOperationLog(bizType = "param", bizName = "系统参数", operationType = "删除", bizId = "#id")
    public void remove(Long id) {
        Map<String, Object> row = detail(id);
        jdbcTemplate.update("update sys_param set deleted=1,update_time=current_timestamp where id=?", id);
        clear(row.get("param_key"));
    }

    private void clear(Object key) {
        StringRedisTemplate redis = redisProvider.getIfAvailable();
        if (key != null && redis != null) {
            try { redis.delete("param:" + key); } catch (RuntimeException ignored) { }
        }
    }
}
