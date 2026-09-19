package com.example.demo.mq.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@TableName("mq_send_message")
@Schema(description = "消息发送记录")
public class MqSendMessage {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "消息唯一标识")
    private String messageId;

    @Schema(description = "消息队列提供者")
    private String provider;

    @Schema(description = "消息类型：NORMAL/RELIABLE")
    private String messageType;

    @Schema(description = "逻辑目标")
    private String destination;

    @Schema(description = "路由键")
    private String routingKey;

    @Schema(description = "消息载荷")
    private String payload;

    @Schema(description = "消息请求头 JSON")
    private String headers;

    @Schema(description = "状态：PENDING/SENDING/FAILED/SENT/MANUAL")
    private Integer status;

    @Schema(description = "发送尝试次数")
    private Integer attemptCount;

    @Schema(description = "最大自动发送次数")
    private Integer maxAttempts;

    @Schema(description = "下次重试时间")
    private Date nextRetryTime;

    @Schema(description = "最近发送尝试时间")
    private Date lastAttemptTime;

    @Schema(description = "发送确认时间")
    private Date sentTime;

    @Schema(description = "最近错误信息")
    private String lastError;

    @Schema(description = "人工处理人")
    private Long handledBy;

    @Schema(description = "人工处理时间")
    private Date handledTime;

    @Schema(description = "人工处理备注")
    private String handleRemark;

    @Version
    @Schema(description = "乐观锁版本")
    private Integer version;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;
}
