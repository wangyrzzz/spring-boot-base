package com.example.demo.mq;

import com.example.demo.mq.model.MqConsumeFailureStatus;
import com.example.demo.mq.model.MqSendStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MqStatusTest {

    @Test
    void sendStatusUsesStableNumericCodesAndDescriptions() {
        assertEquals(1, MqSendStatus.PENDING.getValue());
        assertEquals("待发送", MqSendStatus.PENDING.getDesc());
        assertEquals(2, MqSendStatus.SENDING.getValue());
        assertEquals("发送中", MqSendStatus.SENDING.getDesc());
        assertEquals(3, MqSendStatus.FAILED.getValue());
        assertEquals("发送失败", MqSendStatus.FAILED.getDesc());
        assertEquals(4, MqSendStatus.SENT.getValue());
        assertEquals("已发送", MqSendStatus.SENT.getDesc());
        assertEquals(5, MqSendStatus.MANUAL.getValue());
        assertEquals("人工处理", MqSendStatus.MANUAL.getDesc());
    }

    @Test
    void consumeFailureStatusUsesStableNumericCodesAndDescriptions() {
        assertEquals(1, MqConsumeFailureStatus.PENDING_MANUAL.getValue());
        assertEquals("待人工处理", MqConsumeFailureStatus.PENDING_MANUAL.getDesc());
        assertEquals(2, MqConsumeFailureStatus.RETRY_SUBMITTED.getValue());
        assertEquals("已提交重试", MqConsumeFailureStatus.RETRY_SUBMITTED.getDesc());
        assertEquals(3, MqConsumeFailureStatus.RESOLVED.getValue());
        assertEquals("已解决", MqConsumeFailureStatus.RESOLVED.getDesc());
    }
}
