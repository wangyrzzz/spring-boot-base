package com.example.demo.log;

import com.example.demo.common.Constant;
import com.example.demo.entity.SysLogApi;
import com.example.demo.system.LogApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.mq.api.ConsumerRegistration;
import com.example.demo.mq.api.MessageConsumerRegistry;
import com.example.demo.mq.api.ReceivedMessage;
import lombok.RequiredArgsConstructor;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiLogConsumer {
    private final ObjectMapper objectMapper;
    private final LogApiService logApiService;
    private final MessageConsumerRegistry consumerRegistry;

    @PostConstruct
    public void register() {
        consumerRegistry.register(new ConsumerRegistration(Constant.API_LOG_QUEUE, "apiLogConsumer", this::consume));
    }

    public void consume(ReceivedMessage message) throws Exception {
        SysLogApi apiLog = objectMapper.readValue(message.body(), SysLogApi.class);
        logApiService.saveFromMessage(apiLog);
    }
}
