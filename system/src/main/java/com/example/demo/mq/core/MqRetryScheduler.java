package com.example.demo.mq.core;

import com.example.demo.mq.api.MessageDeliveryType;
import com.example.demo.mq.model.MqRetryProperties;
import com.example.demo.mq.model.MqSendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "infra.rabbitmq", name = "enabled", havingValue = "true")
public class MqRetryScheduler {

    private final MqRetryProperties retryProperties;
    private final MqSendMessageService sendMessageService;
    private final DefaultMessageQueueTemplate messageQueueTemplate;

    @Scheduled(fixedDelayString = "${mq.retry.fixed-delay-ms:180000}")
    public void retryFailedMessages() {
        Date now = new Date();
        sendMessageService.recoverStaleSending(new Date(now.getTime() - retryProperties.getStaleSendingTimeoutMs()));
        for (MqSendMessage message : sendMessageService.listRetryable(now, retryProperties.getBatchSize())) {
            try {
                messageQueueTemplate.dispatchStored(message.getMessageId(), MessageDeliveryType.valueOf(message.getMessageType()));
            } catch (Exception ex) {
                log.error("消息重试调度失败, messageId={}", message.getMessageId(), ex);
            }
        }
    }
}
