package com.example.demo.mq.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@TableName("mq_consume_failure")
@Schema(description = "消息消费失败记录")
public class MqConsumeFailure {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "消息队列提供者")
    private String provider;

    @Schema(description = "原消息唯一标识")
    private String sourceMessageId;

    @Schema(description = "消息类型：NORMAL/RELIABLE")
    private String deliveryType;

    @Schema(description = "逻辑目标")
    private String destination;

    @Schema(description = "路由键")
    private String routingKey;

    @Schema(description = "消费者名称")
    private String consumerName;

    @Schema(description = "消息载荷")
    private String payload;

    @Schema(description = "消息请求头 JSON")
    private String headers;

    @Schema(description = "异常类型")
    private String exceptionType;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "异常堆栈")
    private String stackTrace;

    @Schema(description = "状态：PENDING_MANUAL/RETRY_SUBMITTED/RESOLVED")
    private Integer status;

    @Schema(description = "人工重试次数")
    private Integer retryCount;

    @Schema(description = "最近重试消息 ID")
    private String lastRetryMessageId;

    @Schema(description = "人工处理人")
    private Long handledBy;

    @Schema(description = "人工处理时间")
    private Date handledTime;

    @Schema(description = "人工处理备注")
    private String handleRemark;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;
}
