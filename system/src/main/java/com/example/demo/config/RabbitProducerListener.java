package com.example.demo.config;

import com.example.demo.mq.provider.rabbit.RabbitPublisherCallbackRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @Author: WangYuanrong
 * @Date: 2022/3/30 15:41
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitProducerListener implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    private final RabbitPublisherCallbackRegistry callbackRegistry;

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        callbackRegistry.confirm(correlationData, ack, cause);
        log.info("RabbitMQ消息发布确认: correlationData={}, ack={}, cause={}", correlationData, ack, cause);
    }

    @Override
    public void returnedMessage(ReturnedMessage returned) {
        callbackRegistry.returned(returned);
        log.error("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",
                returned.getExchange(), returned.getRoutingKey(), returned.getReplyCode(), returned.getReplyText(), returned.getMessage());
    }
}
