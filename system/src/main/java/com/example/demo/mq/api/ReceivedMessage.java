package com.example.demo.mq.api;

import java.util.Map;

/**
 * Provider-neutral inbound message. The handler never needs broker-specific
 * delivery tags or channels.
 */
public record ReceivedMessage(
        String messageId,
        String destination,
        String routingKey,
        String body,
        Map<String, String> headers,
        MessageTransportType transportType,
        MessageDeliveryType deliveryType) {

    public ReceivedMessage(String messageId, String destination, String routingKey,
                           String body, Map<String, String> headers,
                           MessageDeliveryType deliveryType) {
        this(messageId, destination, routingKey, body, headers,
                MessageTransportType.RABBITMQ, deliveryType);
    }
}
