package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_log_api")
public class SysLogApi extends BaseEntity {
    private String serviceId;
    private String serverHost;
    private String serverIp;
    private String env;
    private String type;
    private String title;
    private String method;
    private String requestUri;
    private String userAgent;
    private String remoteIp;
    private String methodClass;
    private String methodName;
    private String requestParams;
    private String responseParams;
    private Long durationMs;
    private Integer httpStatus;
    private Integer success;
    private String errorMessage;
    @TableField("is_deleted")
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
