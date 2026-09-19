package com.example.demo.mq.spi;

import com.example.demo.mq.api.ConsumerRegistration;

public interface MessageQueueProvider {

    String name();

    void send(OutboundMessage message, MessageSendCallback callback);

    void registerConsumer(ConsumerRegistration registration);
}
