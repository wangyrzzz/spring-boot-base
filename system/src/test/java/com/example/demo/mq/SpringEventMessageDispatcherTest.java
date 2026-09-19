package com.example.demo.mq;

import com.example.demo.mq.api.ConsumerRegistration;
import com.example.demo.mq.api.MessageDeliveryType;
import com.example.demo.mq.api.MessageTransportType;
import com.example.demo.mq.core.DefaultMessageConsumerRegistry;
import com.example.demo.mq.core.MqConsumeFailureService;
import com.example.demo.mq.provider.event.SpringEventMessage;
import com.example.demo.mq.provider.event.SpringEventMessageDispatcher;
import com.example.demo.mq.spi.OutboundMessage;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SpringEventMessageDispatcherTest {

    @Test
    void dispatchesOnlyToSpringEventConsumers() throws Exception {
        DefaultMessageConsumerRegistry registry = new DefaultMessageConsumerRegistry();
        var eventHandler = mock(com.example.demo.mq.api.MessageHandler.class);
        var rabbitHandler = mock(com.example.demo.mq.api.MessageHandler.class);
        registry.register(new ConsumerRegistration("event.queue", "event", eventHandler,
                MessageTransportType.SPRING_EVENT));
        registry.register(new ConsumerRegistration("event.queue", "rabbit", rabbitHandler,
                MessageTransportType.RABBITMQ));
        SpringEventMessageDispatcher dispatcher = new SpringEventMessageDispatcher(
                registry, mock(MqConsumeFailureService.class));

        dispatcher.onMessage(new SpringEventMessage(new OutboundMessage(
                "message-1", "event.queue", null, "{}", Map.of(), MessageDeliveryType.NORMAL)));

        verify(eventHandler).handle(any());
        org.mockito.Mockito.verifyNoInteractions(rabbitHandler);
    }
}
