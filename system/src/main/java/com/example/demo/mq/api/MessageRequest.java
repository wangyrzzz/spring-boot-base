package com.example.demo.mq.api;

import org.springframework.util.Assert;

import java.util.Map;

/**
 * Provider-neutral outbound message request.
 */
public record MessageRequest(
        String destination,
        String routingKey,
        Object payload,
        Map<String, String> headers) {

    public MessageRequest {
        Assert.hasText(destination, "destination must not be blank");
        headers = headers == null ? Map.of() : Map.copyOf(headers);
    }

    public static MessageRequest of(String destination, Object payload) {
        return new MessageRequest(destination, null, payload, Map.of());
    }
}
