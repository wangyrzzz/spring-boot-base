package com.example.demo.log;

import com.example.demo.common.Constant;
import com.example.demo.entity.SysLogApi;
import com.example.demo.system.LogApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "host")
public class ApiLogConsumer {
    private final ObjectMapper objectMapper;
    private final LogApiService logApiService;

    @RabbitListener(queues = Constant.API_LOG_QUEUE)
    public void consume(String payload, Channel channel, org.springframework.amqp.core.Message message) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            SysLogApi apiLog = objectMapper.readValue(payload, SysLogApi.class);
            logApiService.saveFromMessage(apiLog);
            channel.basicAck(deliveryTag, false);
        } catch (Exception ex) {
            log.error("接口访问日志消费失败，消息丢弃: {}", payload, ex);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
