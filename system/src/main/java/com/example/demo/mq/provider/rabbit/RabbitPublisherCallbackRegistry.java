package com.example.demo.mq.provider.rabbit;

import com.example.demo.mq.spi.MessageSendCallback;
import com.example.demo.mq.spi.OutboundMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class RabbitPublisherCallbackRegistry {

    private final Map<String, PendingPublish> pending = new ConcurrentHashMap<>();

    public CorrelationData register(OutboundMessage message, MessageSendCallback callback) {
        cleanup();
        CorrelationData correlationData = new CorrelationData(message.messageId());
        pending.put(message.messageId(), new PendingPublish(message, callback));
        return correlationData;
    }

    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (correlationData == null || !StringUtils.hasText(correlationData.getId())) {
            return;
        }
        PendingPublish item = pending.get(correlationData.getId());
        if (item == null) {
            return;
        }
        if (ack) {
            if (item.successNotified.compareAndSet(false, true)) {
                item.callback.onSuccess(item.message.messageId());
            }
        } else if (item.failureNotified.compareAndSet(false, true)) {
            pending.remove(item.message.messageId());
            item.callback.onFailure(item.message.messageId(), "RabbitMQ发布确认失败: " + cause);
        }
    }

    public void returned(ReturnedMessage returned) {
        if (returned == null || returned.getMessage() == null) {
            return;
        }
        String messageId = returned.getMessage().getMessageProperties().getMessageId();
        if (!StringUtils.hasText(messageId)) {
            return;
        }
        PendingPublish item = pending.get(messageId);
        if (item == null || !item.failureNotified.compareAndSet(false, true)) {
            return;
        }
        pending.remove(messageId);
        item.callback.onFailure(messageId, "RabbitMQ消息退回: " + returned.getReplyText());
    }

    public void failed(String messageId, String reason) {
        PendingPublish item = pending.remove(messageId);
        if (item != null && item.failureNotified.compareAndSet(false, true)) {
            item.callback.onFailure(messageId, reason);
        }
    }

    private void cleanup() {
        long expireAt = System.currentTimeMillis() - 600_000L;
        pending.entrySet().removeIf(entry -> entry.getValue().createdAt < expireAt);
    }

    private static final class PendingPublish {
        private final OutboundMessage message;
        private final MessageSendCallback callback;
        private final long createdAt = System.currentTimeMillis();
        private final AtomicBoolean successNotified = new AtomicBoolean();
        private final AtomicBoolean failureNotified = new AtomicBoolean();

        private PendingPublish(OutboundMessage message, MessageSendCallback callback) {
            this.message = message;
            this.callback = callback;
        }
    }
}
