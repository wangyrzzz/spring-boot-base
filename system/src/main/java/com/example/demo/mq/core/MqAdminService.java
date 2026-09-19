package com.example.demo.mq.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.common.AuthUserContext;
import com.example.demo.mq.api.MessageDeliveryType;
import com.example.demo.mq.api.MessageQueueTemplate;
import com.example.demo.mq.model.MqConsumeFailure;
import com.example.demo.mq.model.MqConsumeFailureStatus;
import com.example.demo.mq.model.MqSendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MqAdminService {

    private final MqSendMessageService sendMessageService;
    private final MqConsumeFailureService consumeFailureService;
    private final MessageQueueTemplate messageQueueTemplate;
    private final ObjectMapper objectMapper;

    public void retrySend(Long id) {
        MqSendMessage message = sendMessageService.getById(id);
        if (message == null) {
            throw new IllegalArgumentException("发送消息不存在: " + id);
        }
        messageQueueTemplate.resend(message.getMessageId(), MessageDeliveryType.valueOf(message.getMessageType()));
    }

    public void retrySendBatch(Collection<Long> ids) {
        ids.forEach(this::retrySend);
    }

    @Transactional
    public void resolveSend(Long id, String remark) {
        Long operatorId = AuthUserContext.get() == null ? null : AuthUserContext.get().getUserId();
        if (!sendMessageService.resolve(id, operatorId, remark)) {
            throw new IllegalArgumentException("发送消息不存在或已经成功: " + id);
        }
    }

    public void retryConsume(Long id) {
        MqConsumeFailure failure = consumeFailureService.getById(id);
        if (failure == null || MqConsumeFailureStatus.RESOLVED.name().equals(failure.getStatus())) {
            throw new IllegalArgumentException("消费失败记录不存在或已处理: " + id);
        }
        MessageDeliveryType deliveryType = parseDeliveryType(failure.getDeliveryType());
        String messageId = messageQueueTemplate.sendRaw(failure.getDestination(), failure.getRoutingKey(),
                failure.getPayload(), parseHeaders(failure.getHeaders()), deliveryType);
        consumeFailureService.markRetrySubmitted(id, messageId);
    }

    public void retryConsumeBatch(Collection<Long> ids) {
        ids.forEach(this::retryConsume);
    }

    @Transactional
    public void resolveConsume(Long id, String remark) {
        Long operatorId = AuthUserContext.get() == null ? null : AuthUserContext.get().getUserId();
        if (!consumeFailureService.resolve(id, operatorId, remark)) {
            throw new IllegalArgumentException("消费失败记录不存在: " + id);
        }
    }

    private MessageDeliveryType parseDeliveryType(String value) {
        if (value == null) {
            return MessageDeliveryType.NORMAL;
        }
        try {
            return MessageDeliveryType.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return MessageDeliveryType.NORMAL;
        }
    }

    private Map<String, String> parseHeaders(String headers) {
        if (headers == null || headers.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(headers, new TypeReference<>() {
            });
        } catch (Exception ex) {
            return Map.of();
        }
    }
}
