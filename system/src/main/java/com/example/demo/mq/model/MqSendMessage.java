package com.example.demo.mq.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.util.Date;

@Data
@TableName("mq_send_message")
public class MqSendMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String messageId;

    private String provider;

    private String messageType;

    private String destination;

    private String routingKey;

    private String payload;

    private String headers;

    private Integer status;

    private Integer attemptCount;

    private Integer maxAttempts;

    private Date nextRetryTime;

    private Date lastAttemptTime;

    private Date sentTime;

    private String lastError;

    private Long handledBy;

    private Date handledTime;

    private String handleRemark;

    @Version
    private Integer version;

    private Date createTime;

    private Date updateTime;
}
