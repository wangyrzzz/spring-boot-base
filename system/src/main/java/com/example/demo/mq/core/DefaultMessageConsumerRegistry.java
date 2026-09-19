package com.example.demo.mq.core;

import com.example.demo.mq.api.ConsumerRegistration;
import com.example.demo.mq.api.MessageConsumerRegistry;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class DefaultMessageConsumerRegistry implements MessageConsumerRegistry {

    @Getter
    private final List<ConsumerRegistration> registrations = new CopyOnWriteArrayList<>();

    @Override
    public void register(ConsumerRegistration registration) {
        registrations.removeIf(item -> item.destination().equals(registration.destination())
                && item.consumerName().equals(registration.consumerName()));
        registrations.add(registration);
    }
}
