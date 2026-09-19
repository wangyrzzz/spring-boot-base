package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_operation_log")
@Schema(description = "业务操作日志")
public class SysOperationLog extends BaseEntity {
    @Schema(description = "业务类型")
    private String bizType;
    @Schema(description = "业务 ID")
    private String bizId;
    @Schema(description = "业务名称")
    private String bizName;
    @Schema(description = "操作类型")
    private String operationType;
    @Schema(description = "操作人姓名")
    private String operatorName;
    @Schema(description = "部门名称")
    private String departmentName;
    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "客户端 IP")
    private String ip;
    @Schema(description = "设备类型")
    private String deviceType;
    @Schema(description = "请求路径")
    private String requestPath;
    @Schema(description = "HTTP 请求方法")
    private String httpMethod;
    @Schema(description = "方法所属类")
    private String methodClass;
    @Schema(description = "方法名")
    private String methodName;
    @Schema(description = "请求参数")
    private String requestParams;
    @Schema(description = "返回结果")
    private String resultData;
    @Schema(description = "错误信息")
    private String errorMessage;
    @Schema(description = "执行时间，单位：毫秒")
    private Long durationMs;
    @Schema(description = "变更前快照")
    private String beforeSnapshot;
    @Schema(description = "变更后快照")
    private String afterSnapshot;
    @Schema(description = "变更摘要")
    private String changeSummary;
    @Schema(description = "流程节点")
    private String flowNode;
    @Schema(description = "风险标记")
    private String riskFlag;
    @Schema(description = "是否成功：0否，1是")
    private Integer success;
}
