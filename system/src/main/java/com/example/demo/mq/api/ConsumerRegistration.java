package com.example.demo.mq.api;

import org.springframework.util.Assert;

/**
 * A logical consumer registration. A provider maps the logical destination
 * to its own queue/topic and acknowledgement implementation.
 */
public record ConsumerRegistration(
        String destination,
        String consumerName,
        MessageHandler handler,
        MessageTransportType transportType) {

    public ConsumerRegistration(String destination, String consumerName, MessageHandler handler) {
        this(destination, consumerName, handler, MessageTransportType.RABBITMQ);
    }

    public ConsumerRegistration {
        Assert.hasText(destination, "destination must not be blank");
        Assert.hasText(consumerName, "consumerName must not be blank");
        Assert.notNull(handler, "handler must not be null");
        Assert.notNull(transportType, "transportType must not be null");
    }
}
