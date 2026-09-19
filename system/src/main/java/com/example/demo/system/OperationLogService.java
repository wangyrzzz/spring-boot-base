package com.example.demo.system;

import lombok.RequiredArgsConstructor;
import com.example.demo.common.AuthUserContext;
import com.example.demo.common.AuthenticatedUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OperationLogService {
    private final JdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(Map<String, Object> log) {
        jdbcTemplate.update("insert into sys_operation_log (create_by,biz_type,biz_id,biz_name,operation_type,operator_name,department_name,role_name,ip,device_type,request_path,http_method,method_class,method_name,request_params,result_data,error_message,duration_ms,before_snapshot,after_snapshot,change_summary,flow_node,risk_flag,success,create_time,update_time) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,current_timestamp,current_timestamp)",
                log.get("operatorId"), text(log.get("bizType")), text(log.get("bizId")), text(log.get("bizName")), text(log.get("operationType")), text(log.get("operatorName")), text(log.get("departmentName")), text(log.get("roleName")), text(log.get("ip")), text(log.get("deviceType")), text(log.get("requestPath")), text(log.get("httpMethod")), text(log.get("methodClass")), text(log.get("methodName")), text(log.get("requestParams")), text(log.get("resultData")), text(log.get("errorMessage")), log.get("durationMs"), text(log.get("beforeSnapshot")), text(log.get("afterSnapshot")), text(log.get("changeSummary")), text(log.get("flowNode")), text(log.get("riskFlag")), log.getOrDefault("success", 1));
    }

    public List<Map<String,Object>> page(String bizType, String bizId) {
        AuthenticatedUser user = AuthUserContext.get();
        boolean admin = user != null && user.getRoleName() != null && (user.getRoleName().toLowerCase().contains("admin") || user.getRoleName().contains("管理员"));
        if (!admin && user == null) return List.of();
        if (bizType == null && bizId == null) return admin
                ? jdbcTemplate.queryForList("select * from sys_operation_log order by id desc limit 200")
                : jdbcTemplate.queryForList("select * from sys_operation_log where create_by=? order by id desc limit 200", user.getUserId());
        return admin
                ? jdbcTemplate.queryForList("select * from sys_operation_log where (? is null or biz_type=?) and (? is null or biz_id=?) order by id desc", bizType, bizType, bizId, bizId)
                : jdbcTemplate.queryForList("select * from sys_operation_log where create_by=? and (? is null or biz_type=?) and (? is null or biz_id=?) order by id desc", user.getUserId(), bizType, bizType, bizId, bizId);
    }
    public Map<String,Object> detail(Long id) {
        AuthenticatedUser user = AuthUserContext.get();
        if (!isAdmin(user) && user == null) return Map.of();
        return isAdmin(user) ? jdbcTemplate.queryForMap("select * from sys_operation_log where id=?", id)
                : jdbcTemplate.queryForMap("select * from sys_operation_log where id=? and create_by=?", id, user.getUserId());
    }
    public List<Map<String,Object>> types() {
        AuthenticatedUser user = AuthUserContext.get();
        return isAdmin(user) ? jdbcTemplate.queryForList("select distinct biz_type from sys_operation_log where biz_type is not null order by biz_type")
                : jdbcTemplate.queryForList("select distinct biz_type from sys_operation_log where biz_type is not null and create_by=? order by biz_type", user == null ? null : user.getUserId());
    }
    public List<Map<String,Object>> statistics() {
        AuthenticatedUser user = AuthUserContext.get();
        return isAdmin(user) ? jdbcTemplate.queryForList("select biz_type,count(*) total_count,sum(success=1) success_count,sum(success=0) failure_count from sys_operation_log group by biz_type order by total_count desc")
                : jdbcTemplate.queryForList("select biz_type,count(*) total_count,sum(success=1) success_count,sum(success=0) failure_count from sys_operation_log where create_by=? group by biz_type order by total_count desc", user == null ? null : user.getUserId());
    }
    private boolean isAdmin(AuthenticatedUser user) { return user != null && user.getRoleName() != null && (user.getRoleName().toLowerCase().contains("admin") || user.getRoleName().contains("管理员")); }
    private String text(Object value) { return value == null ? null : String.valueOf(value); }
}
