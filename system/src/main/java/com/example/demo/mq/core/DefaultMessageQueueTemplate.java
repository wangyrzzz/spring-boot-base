package com.example.demo.mq.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.mq.api.MessageDeliveryType;
import com.example.demo.mq.api.MessageQueueTemplate;
import com.example.demo.mq.api.MessageRequest;
import com.example.demo.mq.api.MessageTransportType;
import com.example.demo.mq.model.MqSendMessage;
import com.example.demo.mq.provider.event.SpringEventMessage;
import com.example.demo.mq.spi.MessageQueueProvider;
import com.example.demo.mq.spi.MessageSendCallback;
import com.example.demo.mq.spi.OutboundMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class DefaultMessageQueueTemplate implements MessageQueueTemplate {

    private final ObjectProvider<MessageQueueProvider> providerProvider;
    private final ObjectMapper objectMapper;
    private final MqSendMessageService sendMessageService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public DefaultMessageQueueTemplate(ObjectProvider<MessageQueueProvider> providerProvider,
                                       ObjectMapper objectMapper,
                                       MqSendMessageService sendMessageService,
                                       ApplicationEventPublisher applicationEventPublisher) {
        this.providerProvider = providerProvider;
        this.objectMapper = objectMapper;
        this.sendMessageService = sendMessageService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public DefaultMessageQueueTemplate(ObjectProvider<MessageQueueProvider> providerProvider,
                                       ObjectMapper objectMapper,
                                       MqSendMessageService sendMessageService) {
        this(providerProvider, objectMapper, sendMessageService, event -> {
        });
    }

    @Override
    public String send(MessageRequest request) {
        return send(request, MessageTransportType.RABBITMQ, MessageDeliveryType.NORMAL);
    }

    @Override
    public String send(MessageRequest request, MessageTransportType transportType) {
        return send(request, transportType, MessageDeliveryType.NORMAL);
    }

    @Override
    public String send(MessageRequest request, MessageTransportType transportType,
                       MessageDeliveryType deliveryType) {
        return sendSerialized(request, normalizeTransport(transportType), normalizeDelivery(deliveryType));
    }

    @Override
    public String sendReliable(MessageRequest request) {
        return send(request, MessageTransportType.RABBITMQ, MessageDeliveryType.RELIABLE);
    }

    @Override
    public String sendRaw(String destination, String routingKey, String body,
                          Map<String, String> headers, MessageDeliveryType deliveryType) {
        return sendRaw(destination, routingKey, body, headers,
                MessageTransportType.RABBITMQ, deliveryType);
    }

    @Override
    public String sendRaw(String destination, String routingKey, String body,
                          Map<String, String> headers, MessageTransportType transportType,
                          MessageDeliveryType deliveryType) {
        transportType = normalizeTransport(transportType);
        deliveryType = normalizeDelivery(deliveryType);
        validateDelivery(transportType, deliveryType);
        OutboundMessage message = new OutboundMessage(UUID.randomUUID().toString(), destination,
                routingKey, body, headers == null ? Map.of() : Map.copyOf(headers), deliveryType);
        if (deliveryType == MessageDeliveryType.RELIABLE) {
            persistAndDispatchAfterCommit(message);
        } else {
            dispatch(message, transportType);
        }
        return message.messageId();
    }

    @Override
    public String resend(String messageId, MessageDeliveryType deliveryType) {
        MqSendMessage stored = sendMessageService.getByMessageId(messageId);
        if (stored == null) {
            throw new IllegalArgumentException("消息不存在: " + messageId);
        }
        if (!sendMessageService.resetForManualRetry(stored.getId())) {
            return messageId;
        }
        dispatchStored(messageId, deliveryType == null ? MessageDeliveryType.valueOf(stored.getMessageType()) : deliveryType);
        return messageId;
    }

    public void dispatchStored(String messageId, MessageDeliveryType deliveryType) {
        MqSendMessage stored = sendMessageService.getByMessageId(messageId);
        if (stored == null || !sendMessageService.claimForSend(stored.getId())) {
            return;
        }
        OutboundMessage outbound = new OutboundMessage(stored.getMessageId(), stored.getDestination(),
                stored.getRoutingKey(), stored.getPayload(), parseHeaders(stored.getHeaders()), deliveryType);
        dispatch(outbound, MessageTransportType.RABBITMQ);
    }

    private String sendSerialized(MessageRequest request, MessageTransportType transportType,
                                  MessageDeliveryType deliveryType) {
        validateDelivery(transportType, deliveryType);
        String messageId = UUID.randomUUID().toString();
        String body;
        try {
            body = objectMapper.writeValueAsString(request.payload());
        } catch (JsonProcessingException ex) {
            OutboundMessage failed = new OutboundMessage(messageId, request.destination(), request.routingKey(),
                    String.valueOf(request.payload()), request.headers(), deliveryType);
            if (deliveryType == MessageDeliveryType.NORMAL) {
                sendMessageService.markFailure(failed, "消息序列化失败: " + ex.getMessage());
                return messageId;
            }
            throw new IllegalArgumentException("可靠消息序列化失败", ex);
        }
        OutboundMessage message = new OutboundMessage(messageId, request.destination(), request.routingKey(),
                body, request.headers(), deliveryType);
        if (deliveryType == MessageDeliveryType.RELIABLE) {
            persistAndDispatchAfterCommit(message);
        } else {
            dispatch(message, transportType);
        }
        return messageId;
    }

    private void persistAndDispatchAfterCommit(OutboundMessage message) {
        MqSendMessage stored = sendMessageService.fromOutbound(message);
        stored.setNextRetryTime(new java.util.Date());
        sendMessageService.savePending(stored);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    dispatchStored(message.messageId(), message.deliveryType());
                }
            });
        } else {
            dispatchStored(message.messageId(), message.deliveryType());
        }
    }

    private void dispatch(OutboundMessage message, MessageTransportType transportType) {
        if (transportType == MessageTransportType.SPRING_EVENT) {
            try {
                applicationEventPublisher.publishEvent(new SpringEventMessage(message));
            } catch (RuntimeException ex) {
                log.error("Spring Event消息发送失败, messageId={}", message.messageId(), ex);
                sendMessageService.markFailure(message, ex.getMessage());
            }
            return;
        }
        MessageQueueProvider provider = providerProvider.getIfAvailable();
        if (provider == null) {
            sendMessageService.markFailure(message, "没有可用的消息队列提供者");
            return;
        }
        try {
            provider.send(message, new MessageSendCallback() {
                @Override
                public void onSuccess(String messageId) {
                    sendMessageService.markSuccess(messageId);
                }

                @Override
                public void onFailure(String messageId, String reason) {
                    sendMessageService.markFailure(message, reason);
                }
            });
        } catch (RuntimeException ex) {
            log.error("消息发送调用失败, messageId={}", message.messageId(), ex);
            sendMessageService.markFailure(message, ex.getMessage());
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
            log.warn("消息请求头解析失败，将使用空请求头, headers={}", headers, ex);
            return Map.of();
        }
    }

    private MessageTransportType normalizeTransport(MessageTransportType transportType) {
        return transportType == null ? MessageTransportType.RABBITMQ : transportType;
    }

    private MessageDeliveryType normalizeDelivery(MessageDeliveryType deliveryType) {
        return deliveryType == null ? MessageDeliveryType.NORMAL : deliveryType;
    }

    private void validateDelivery(MessageTransportType transportType, MessageDeliveryType deliveryType) {
        if (transportType == MessageTransportType.SPRING_EVENT
                && deliveryType == MessageDeliveryType.RELIABLE) {
            throw new IllegalArgumentException("Spring Event仅支持普通消息，可靠消息必须使用RabbitMQ");
        }
    }
}
