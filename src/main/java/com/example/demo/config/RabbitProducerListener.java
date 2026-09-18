package com.example.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: WangYuanrong
 * @Date: 2022/3/30 15:41
 */
@Component
@Slf4j
public class RabbitProducerListener implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
    }

    @Override
    public void returnedMessage(ReturnedMessage returned) {
        log.error("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",
                returned.getExchange(), returned.getRoutingKey(), returned.getReplyCode(), returned.getReplyText(), returned.getMessage());
    }
}
