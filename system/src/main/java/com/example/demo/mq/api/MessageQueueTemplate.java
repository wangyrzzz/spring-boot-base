package com.example.demo.mq.api;

/**
 * Application-facing message queue abstraction.
 */
public interface MessageQueueTemplate {

    String send(MessageRequest request);

    String send(MessageRequest request, MessageTransportType transportType);

    String send(MessageRequest request, MessageTransportType transportType,
                MessageDeliveryType deliveryType);

    String sendReliable(MessageRequest request);

    String sendRaw(String destination, String routingKey, String body,
                   java.util.Map<String, String> headers, MessageDeliveryType deliveryType);

    String sendRaw(String destination, String routingKey, String body,
                   java.util.Map<String, String> headers,
                   MessageTransportType transportType, MessageDeliveryType deliveryType);

    /**
     * Re-publish a stored message with the requested delivery semantics.
     * This is used by operator recovery and the retry scheduler.
     */
    String resend(String messageId, MessageDeliveryType deliveryType);
}
