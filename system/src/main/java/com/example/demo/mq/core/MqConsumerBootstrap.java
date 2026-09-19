package com.example.demo.mq.core;

import com.example.demo.mq.spi.MessageQueueProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqConsumerBootstrap implements SmartInitializingSingleton {

    private final DefaultMessageConsumerRegistry registry;
    private final ObjectProvider<MessageQueueProvider> providerProvider;

    @Override
    public void afterSingletonsInstantiated() {
        MessageQueueProvider provider = providerProvider.getIfAvailable();
        if (provider == null) {
            log.info("没有可用的消息队列提供者，跳过消息消费者注册");
            return;
        }
        registry.getRegistrations().forEach(provider::registerConsumer);
    }
}
