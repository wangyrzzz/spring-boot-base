package com.example.demo.mq.provider.event;

import com.example.demo.mq.api.ConsumerRegistration;
import com.example.demo.mq.api.MessageTransportType;
import com.example.demo.mq.api.ReceivedMessage;
import com.example.demo.mq.core.DefaultMessageConsumerRegistry;
import com.example.demo.mq.core.MqConsumeFailureService;
import com.example.demo.mq.spi.OutboundMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Dispatches in-process message events asynchronously to event consumers.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringEventMessageDispatcher {

    private final DefaultMessageConsumerRegistry registry;
    private final MqConsumeFailureService consumeFailureService;

    @Async("asyncTaskExecutor")
    @EventListener
    public void onMessage(SpringEventMessage event) {
        OutboundMessage outbound = event.message();
        List<ConsumerRegistration> consumers = registry.getRegistrations().stream()
                .filter(registration -> registration.transportType() == MessageTransportType.SPRING_EVENT)
                .filter(registration -> registration.destination().equals(outbound.destination()))
                .toList();
        if (consumers.isEmpty()) {
            log.warn("没有可用的Spring Event消费者, destination={}, messageId={}",
                    outbound.destination(), outbound.messageId());
            return;
        }
        ReceivedMessage received = new ReceivedMessage(outbound.messageId(), outbound.destination(),
                outbound.routingKey(), outbound.body(), outbound.headers(),
                MessageTransportType.SPRING_EVENT, outbound.deliveryType());
        for (ConsumerRegistration consumer : consumers) {
            try {
                consumer.handler().handle(received);
            } catch (Throwable error) {
                try {
                    consumeFailureService.record(received, consumer.consumerName(), error);
                } catch (Throwable recordError) {
                    log.error("Spring Event消费失败记录落库失败, destination={}, messageId={}",
                            outbound.destination(), outbound.messageId(), recordError);
                }
            }
        }
    }
}
