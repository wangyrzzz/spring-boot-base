package com.example.demo.log;

import com.example.demo.common.Constant;
import com.example.demo.entity.SysLogApi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.RejectedExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiLogPublisher {
    private final ObjectProvider<RabbitTemplate> rabbitTemplateProvider;
    private final ObjectMapper objectMapper;
    private final AsyncTaskExecutor asyncTaskExecutor;

    public void publish(SysLogApi apiLog) {
        RabbitTemplate rabbitTemplate = rabbitTemplateProvider.getIfAvailable();
        if (rabbitTemplate == null) {
            log.warn("RabbitMQ未配置，丢弃接口访问日志");
            return;
        }
        try {
            asyncTaskExecutor.submit(() -> send(apiLog, rabbitTemplate));
        } catch (RejectedExecutionException ex) {
            log.error("接口访问日志异步任务被拒绝，日志丢弃", ex);
        } catch (RuntimeException ex) {
            log.error("提交接口访问日志异步任务失败，日志丢弃", ex);
        }
    }

    private void send(SysLogApi apiLog, RabbitTemplate rabbitTemplate) {
        try {
            String payload = objectMapper.writeValueAsString(apiLog);
            rabbitTemplate.convertAndSend(Constant.API_LOG_QUEUE, payload);
        } catch (JsonProcessingException ex) {
            log.error("接口访问日志序列化失败，日志丢弃", ex);
        } catch (RuntimeException ex) {
            log.error("接口访问日志发送RabbitMQ失败，日志丢弃", ex);
        }
    }
}
