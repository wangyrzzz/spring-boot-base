package com.example.demo.mq.core;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.mq.api.MessageDeliveryType;
import com.example.demo.mq.model.MqRetryProperties;
import com.example.demo.mq.model.MqProperties;
import com.example.demo.mq.model.MqSendMessage;
import com.example.demo.mq.model.MqSendStatus;
import com.example.demo.mapper.MqSendMessageMapper;
import com.example.demo.mq.spi.OutboundMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MqSendMessageService extends ServiceImpl<MqSendMessageMapper, MqSendMessage> {

    private final MqRetryProperties retryProperties;

    private final MqProperties mqProperties;

    @Transactional
    public void savePending(MqSendMessage message) {
        Date now = new Date();
        if (message.getCreateTime() == null) {
            message.setCreateTime(now);
        }
        message.setUpdateTime(now);
        if (message.getVersion() == null) {
            message.setVersion(0);
        }
        save(message);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSuccess(String messageId) {
        MqSendMessage message = getByMessageId(messageId);
        if (message == null) {
            return;
        }
        message.setStatus(MqSendStatus.SENT.getValue());
        message.setSentTime(new Date());
        message.setNextRetryTime(null);
        message.setLastError(null);
        message.setUpdateTime(new Date());
        updateById(message);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailure(OutboundMessage outboundMessage, String reason) {
        MqSendMessage message = getByMessageId(outboundMessage.messageId());
        if (message == null) {
            message = fromOutbound(outboundMessage);
            message.setAttemptCount(1);
            message.setStatus(MqSendStatus.FAILED.getValue());
            message.setNextRetryTime(nextRetryTime());
            message.setLastError(reason);
            savePending(message);
            return;
        }
        int attempts = message.getAttemptCount() == null ? 0 : message.getAttemptCount();
        int maxAttempts = maxAttempts(message);
        message.setAttemptCount(Math.max(1, attempts));
        message.setLastError(truncate(reason));
        message.setStatus(message.getAttemptCount() >= maxAttempts
                ? MqSendStatus.MANUAL.getValue() : MqSendStatus.FAILED.getValue());
        message.setNextRetryTime(MqSendStatus.FAILED.getValue().equals(message.getStatus())
                ? nextRetryTime() : null);
        message.setUpdateTime(new Date());
        updateById(message);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean claimForSend(Long id) {
        MqSendMessage message = getById(id);
        if (message == null || !isRetryable(message)) {
            return false;
        }
        int attempts = message.getAttemptCount() == null ? 0 : message.getAttemptCount();
        int maxAttempts = maxAttempts(message);
        if (attempts >= maxAttempts) {
            message.setStatus(MqSendStatus.MANUAL.getValue());
            message.setNextRetryTime(null);
            message.setUpdateTime(new Date());
            updateById(message);
            return false;
        }
        message.setStatus(MqSendStatus.SENDING.getValue());
        message.setAttemptCount(attempts + 1);
        message.setLastAttemptTime(new Date());
        message.setNextRetryTime(null);
        message.setUpdateTime(new Date());
        return updateById(message);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recoverStaleSending(Date threshold) {
        List<MqSendMessage> stale = list(new LambdaQueryWrapper<MqSendMessage>()
                .eq(MqSendMessage::getStatus, MqSendStatus.SENDING.getValue())
                .lt(MqSendMessage::getLastAttemptTime, threshold));
        for (MqSendMessage message : stale) {
            int attempts = message.getAttemptCount() == null ? 0 : message.getAttemptCount();
            message.setStatus(attempts >= maxAttempts(message)
                    ? MqSendStatus.MANUAL.getValue() : MqSendStatus.FAILED.getValue());
            message.setNextRetryTime(MqSendStatus.FAILED.getValue().equals(message.getStatus())
                    ? nextRetryTime() : null);
            message.setLastError("发送处理中超时，已恢复为可重试状态");
            message.setUpdateTime(new Date());
            updateById(message);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean resetForManualRetry(Long id) {
        MqSendMessage message = getById(id);
        if (message == null || MqSendStatus.SENT.getValue().equals(message.getStatus())) {
            return false;
        }
        message.setAttemptCount(0);
        message.setStatus(MqSendStatus.PENDING.getValue());
        message.setNextRetryTime(new Date());
        message.setLastError(null);
        message.setUpdateTime(new Date());
        return updateById(message);
    }

    @Transactional
    public boolean resolve(Long id, Long operatorId, String remark) {
        MqSendMessage message = getById(id);
        if (message == null || MqSendStatus.SENT.getValue().equals(message.getStatus())) {
            return false;
        }
        message.setStatus(MqSendStatus.MANUAL.getValue());
        message.setHandledBy(operatorId);
        message.setHandledTime(new Date());
        message.setHandleRemark(remark);
        message.setNextRetryTime(null);
        message.setUpdateTime(new Date());
        return updateById(message);
    }

    public MqSendMessage getByMessageId(String messageId) {
        return getOne(new LambdaQueryWrapper<MqSendMessage>()
                .eq(MqSendMessage::getMessageId, messageId)
                .last("limit 1"), false);
    }

    public List<MqSendMessage> listRetryable(Date now, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 1000));
        return list(new LambdaQueryWrapper<MqSendMessage>()
                .in(MqSendMessage::getStatus, List.of(MqSendStatus.PENDING.getValue(), MqSendStatus.FAILED.getValue()))
                .le(MqSendMessage::getNextRetryTime, now)
                .orderByAsc(MqSendMessage::getNextRetryTime)
                .orderByAsc(MqSendMessage::getId)
                .last("limit " + safeLimit));
    }

    public Page<MqSendMessage> page(long current, long size, Integer status, String messageType,
                                    String destination, String messageId) {
        LambdaQueryWrapper<MqSendMessage> wrapper = new LambdaQueryWrapper<MqSendMessage>()
                .eq(status != null, MqSendMessage::getStatus, status)
                .eq(StringUtils.hasText(messageType), MqSendMessage::getMessageType, messageType)
                .like(StringUtils.hasText(destination), MqSendMessage::getDestination, destination)
                .eq(StringUtils.hasText(messageId), MqSendMessage::getMessageId, messageId)
                .orderByDesc(MqSendMessage::getCreateTime)
                .orderByDesc(MqSendMessage::getId);
        return page(new Page<>(current, size), wrapper);
    }

    private boolean isRetryable(MqSendMessage message) {
        return (MqSendStatus.PENDING.getValue().equals(message.getStatus())
                || MqSendStatus.FAILED.getValue().equals(message.getStatus()))
                && (message.getNextRetryTime() == null || !message.getNextRetryTime().after(new Date()));
    }

    private int maxAttempts(MqSendMessage message) {
        return message.getMaxAttempts() == null || message.getMaxAttempts() <= 0
                ? retryProperties.getMaxAttempts() : message.getMaxAttempts();
    }

    private Date nextRetryTime() {
        return new Date(System.currentTimeMillis() + retryProperties.getFixedDelayMs());
    }

    public MqSendMessage fromOutbound(OutboundMessage outboundMessage) {
        MqSendMessage message = new MqSendMessage();
        message.setMessageId(outboundMessage.messageId());
        message.setProvider(mqProperties.getProvider());
        message.setMessageType(outboundMessage.deliveryType().name());
        message.setDestination(outboundMessage.destination());
        message.setRoutingKey(outboundMessage.routingKey());
        message.setPayload(outboundMessage.body());
        message.setHeaders(outboundMessage.headers() == null ? "{}" : JSON.toJSONString(outboundMessage.headers()));
        message.setStatus(MqSendStatus.PENDING.getValue());
        message.setAttemptCount(0);
        message.setMaxAttempts(retryProperties.getMaxAttempts());
        message.setVersion(0);
        return message;
    }

    private String truncate(String reason) {
        if (!StringUtils.hasText(reason)) {
            return "消息发送失败";
        }
        return reason.length() <= 4000 ? reason : reason.substring(0, 4000);
    }
}
