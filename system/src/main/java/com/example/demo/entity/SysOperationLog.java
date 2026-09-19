package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_operation_log")
public class SysOperationLog extends BaseEntity {
    private String bizType;
    private String bizId;
    private String bizName;
    private String operationType;
    private String operatorName;
    private String departmentName;
    private String roleName;
    private String ip;
    private String deviceType;
    private String requestPath;
    private String httpMethod;
    private String methodClass;
    private String methodName;
    private String requestParams;
    private String resultData;
    private String errorMessage;
    private Long durationMs;
    private String beforeSnapshot;
    private String afterSnapshot;
    private String changeSummary;
    private String flowNode;
    private String riskFlag;
    private Integer success;
}
