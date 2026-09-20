package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.common.AuthUserContext;
import com.example.demo.common.AuthenticatedUser;
import com.example.demo.entity.SysOperationLog;
import com.example.demo.enums.SuccessFlagEnum;
import com.example.demo.mapper.SysOperationLogMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperationLogService extends ServiceImpl<SysOperationLogMapper, SysOperationLog> {
    private static final int MAX_QUERY_COUNT = 200;

    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(Map<String, Object> input) {
        SysOperationLog log = objectMapper.convertValue(input, SysOperationLog.class);
        log.setCreateBy(number(input.get("operatorId")));
        if (log.getSuccess() == null) {
            log.setSuccess(SuccessFlagEnum.SUCCESS.getCode());
        }
        save(log);
    }

    public List<Map<String, Object>> page(String bizType, String bizId) {
        AuthenticatedUser user = AuthUserContext.get();
        if (user == null && !isAdmin(user)) {
            return List.of();
        }
        LambdaQueryWrapper<SysOperationLog> wrapper = queryWrapper(user, bizType, bizId)
                .orderByDesc(SysOperationLog::getId).last("limit " + MAX_QUERY_COUNT);
        return toMaps(list(wrapper));
    }

    public Map<String, Object> detail(Long id) {
        AuthenticatedUser user = AuthUserContext.get();
        if (user == null && !isAdmin(user)) {
            return Map.of();
        }
        SysOperationLog log = getOne(queryWrapper(user, null, null).eq(SysOperationLog::getId, id));
        return log == null ? Map.of() : objectMapper.convertValue(log, new TypeReference<Map<String, Object>>() {
        });
    }

    public List<Map<String, Object>> types() {
        AuthenticatedUser user = AuthUserContext.get();
        if (user == null && !isAdmin(user)) {
            return List.of();
        }
        return list(queryWrapper(user, null, null)).stream()
                .map(SysOperationLog::getBizType).filter(StringUtils::hasText).distinct().sorted()
                .map(value -> Map.<String, Object>of("bizType", value)).toList();
    }

    public List<Map<String, Object>> statistics() {
        AuthenticatedUser user = AuthUserContext.get();
        if (user == null && !isAdmin(user)) {
            return List.of();
        }
        Map<String, List<SysOperationLog>> grouped = list(queryWrapper(user, null, null)).stream()
                .filter(item -> StringUtils.hasText(item.getBizType()))
                .collect(Collectors.groupingBy(SysOperationLog::getBizType, LinkedHashMap::new, Collectors.toList()));
        return grouped.entrySet().stream().map(entry -> {
            long success = entry.getValue().stream().filter(item -> Integer.valueOf(SuccessFlagEnum.SUCCESS.getCode()).equals(item.getSuccess())).count();
            long failure = entry.getValue().size() - success;
            return Map.<String, Object>of("bizType", entry.getKey(), "totalCount", entry.getValue().size(),
                    "successCount", success, "failureCount", failure);
        }).toList();
    }

    private LambdaQueryWrapper<SysOperationLog> queryWrapper(AuthenticatedUser user, String bizType, String bizId) {
        boolean admin = isAdmin(user);
        return new LambdaQueryWrapper<SysOperationLog>()
                .eq(!admin && user != null, SysOperationLog::getCreateBy, user == null ? null : user.getUserId())
                .eq(StringUtils.hasText(bizType), SysOperationLog::getBizType, bizType)
                .eq(StringUtils.hasText(bizId), SysOperationLog::getBizId, bizId);
    }

    private List<Map<String, Object>> toMaps(List<SysOperationLog> logs) {
        return logs.stream().map(this::toMap).toList();
    }

    private Map<String, Object> toMap(SysOperationLog log) {
        return objectMapper.convertValue(log, new TypeReference<Map<String, Object>>() {
        });
    }

    private boolean isAdmin(AuthenticatedUser user) {
        return user != null && user.getRoleName() != null
                && (user.getRoleName().toLowerCase().contains("admin") || user.getRoleName().contains("管理员"));
    }

    private Long number(Object value) {
        return value == null ? null : Long.valueOf(String.valueOf(value));
    }
}
