package com.example.demo.mq.api;

/**
 * Application-facing message queue abstraction.
 */
public interface MessageQueueTemplate {

    String send(MessageRequest request);

    String sendReliable(MessageRequest request);

    String sendRaw(String destination, String routingKey, String body,
                   java.util.Map<String, String> headers, MessageDeliveryType deliveryType);

    /**
     * Re-publish a stored message with the requested delivery semantics.
     * This is used by operator recovery and the retry scheduler.
     */
    String resend(String messageId, MessageDeliveryType deliveryType);
}
