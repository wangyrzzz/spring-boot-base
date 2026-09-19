package com.example.demo.mq.provider.event;

import com.example.demo.mq.spi.OutboundMessage;
import org.springframework.util.Assert;

/**
 * In-process Spring Event envelope for provider-neutral messages.
 */
public record SpringEventMessage(OutboundMessage message) {

    public SpringEventMessage {
        Assert.notNull(message, "message must not be null");
    }
}
