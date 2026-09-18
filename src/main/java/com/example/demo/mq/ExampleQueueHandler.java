package com.example.demo.mq;

import com.alibaba.fastjson2.JSON;
import com.example.demo.common.Constant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RabbitListener(queues = Constant.EXAMPLE_QUEUE)
@Component
public class ExampleQueueHandler {


    @RabbitHandler
    public void handlerManualAck(Object dto, Message message, Channel channel) {
        //  如果手动ACK,消息会被监听消费,但是消息在队列中依旧存在,如果 未配置 acknowledge-mode 默认是会在消费完毕后自动ACK掉
        try {
            log.info("处理队列，接收消息：{}", JSON.toJSONString(dto));
            // 通知 MQ 消息已被成功消费,可以ACK了
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("队列处理异常, 重新压入队列:{}" + JSON.toJSONString(dto), e);
            try {
                // 处理失败,重新压入MQ
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException e1) {
                log.error(e1.getMessage(), e1);
            }
        }
    }
}
