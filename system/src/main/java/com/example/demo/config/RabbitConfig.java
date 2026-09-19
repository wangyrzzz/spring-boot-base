package com.example.demo.config;

import com.example.demo.common.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列配置
 * @Author: WangYuanrong
 * @Date: 2022/3/22 17:16
 */
@Slf4j
@Configuration
@ConditionalOnProperty("spring.rabbitmq.host")
public class RabbitConfig {

    @Autowired
    private RabbitProducerListener rabbitProducerListener;

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setObservationEnabled(true);
        rabbitTemplate.setConfirmCallback(rabbitProducerListener);
        rabbitTemplate.setReturnsCallback(rabbitProducerListener);
        return rabbitTemplate;
    }


    /**
     * 延迟队列交换器, x-delayed-type 和 x-delayed-message 固定
     */
//    @Bean
//    public CustomExchange delayExchange() {
//        Map<String, Object> args = Maps.newHashMap();
//        args.put("x-delayed-type", "direct");
//        return new CustomExchange(RabbitConstant.DELAY_MODE_QUEUE, "x-delayed-message", true, false, args);
//    }
//

    /**
     * 队列
     */
    @Bean
    public Queue exampleQueue() {
        return new Queue(Constant.EXAMPLE_QUEUE, true);
    }

    @Bean
    public Queue apiLogQueue() {
        return new Queue(Constant.API_LOG_QUEUE, true);
    }



}
