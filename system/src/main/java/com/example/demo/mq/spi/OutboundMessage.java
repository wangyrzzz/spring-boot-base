package com.example.demo.mq.spi;

import com.example.demo.mq.api.MessageDeliveryType;

import java.util.Map;

/**
 * Serialized message passed from the core layer to a provider adapter.
 */
public record OutboundMessage(
        String messageId,
        String destination,
        String routingKey,
        String body,
        Map<String, String> headers,
        MessageDeliveryType deliveryType) {
}
