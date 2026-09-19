package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_log_api")
@Schema(description = "接口访问日志")
public class SysLogApi extends BaseEntity {
    @Schema(description = "服务 ID")
    private String serviceId;
    @Schema(description = "服务器名")
    private String serverHost;
    @Schema(description = "服务器 IP 地址")
    private String serverIp;
    @Schema(description = "运行环境")
    private String env;
    @Schema(description = "日志类型")
    private String type;
    @Schema(description = "日志标题")
    private String title;
    @Schema(description = "HTTP 请求方法")
    private String method;
    @Schema(description = "请求 URI")
    private String requestUri;
    @Schema(description = "用户代理")
    private String userAgent;
    @Schema(description = "客户端 IP 地址")
    private String requestIp;
    @Schema(description = "控制器类")
    private String methodClass;
    @Schema(description = "控制器方法")
    private String methodName;
    @Schema(description = "请求参数")
    private String requestParams;
    @Schema(description = "响应参数")
    private String responseParams;
    @Schema(description = "执行时间，单位：毫秒")
    private Long durationMs;
    @Schema(description = "HTTP 状态码")
    private Integer httpStatus;
    @Schema(description = "是否成功：0否，1是")
    private Integer success;
    @Schema(description = "错误信息")
    private String errorMessage;
    @TableLogic(value = "0", delval = "1")
    @Schema(description = "软删除：0正常，1已删除")
    private Integer deleted;
}
