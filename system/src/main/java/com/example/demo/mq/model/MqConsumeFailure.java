package com.example.demo.mq.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("mq_consume_failure")
public class MqConsumeFailure {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String provider;

    private String sourceMessageId;

    private String deliveryType;

    private String destination;

    private String routingKey;

    private String consumerName;

    private String payload;

    private String headers;

    private String exceptionType;

    private String errorMessage;

    private String stackTrace;

    private Integer status;

    private Integer retryCount;

    private String lastRetryMessageId;

    private Long handledBy;

    private Date handledTime;

    private String handleRemark;

    private Date createTime;

    private Date updateTime;
}
