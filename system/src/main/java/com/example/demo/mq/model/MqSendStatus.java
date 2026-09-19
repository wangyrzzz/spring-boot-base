package com.example.demo.mq.model;

import lombok.Getter;

@Getter
public enum MqSendStatus {
    PENDING(1, "待发送"),
    SENDING(2, "发送中"),
    FAILED(3, "发送失败"),
    SENT(4, "已发送"),
    MANUAL(5, "人工处理");

    private final Integer value;
    private final String desc;

    MqSendStatus(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
