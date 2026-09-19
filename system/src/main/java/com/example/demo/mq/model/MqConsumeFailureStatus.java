package com.example.demo.mq.model;

import lombok.Getter;

@Getter
public enum MqConsumeFailureStatus {
    PENDING_MANUAL(1, "待人工处理"),
    RETRY_SUBMITTED(2, "已提交重试"),
    RESOLVED(3, "已解决");

    private final Integer value;
    private final String desc;

    MqConsumeFailureStatus(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
