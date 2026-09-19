package com.example.demo.mq;

import com.example.demo.common.Constant;
import com.example.demo.mq.api.ConsumerRegistration;
import com.example.demo.mq.api.MessageConsumerRegistry;
import com.example.demo.mq.api.ReceivedMessage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExampleQueueHandler {

    private final MessageConsumerRegistry consumerRegistry;

    @PostConstruct
    public void register() {
        consumerRegistry.register(new ConsumerRegistration(Constant.EXAMPLE_QUEUE, "exampleQueueConsumer", this::handle));
    }

    public void handle(ReceivedMessage message) {
        log.info("处理队列，接收消息：{}", message.body());
    }
}
