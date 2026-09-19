package com.example.demo.log;

import com.example.demo.common.Constant;
import com.example.demo.entity.SysLogApi;
import com.example.demo.mq.api.MessageQueueTemplate;
import com.example.demo.mq.api.MessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.RejectedExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiLogPublisher {
    private final MessageQueueTemplate messageQueueTemplate;
    private final AsyncTaskExecutor asyncTaskExecutor;

    public void publish(SysLogApi apiLog) {
        try {
            asyncTaskExecutor.submit(() -> messageQueueTemplate.send(MessageRequest.of(Constant.API_LOG_QUEUE, apiLog)));
        } catch (RejectedExecutionException ex) {
            log.error("接口访问日志消息提交线程池失败", ex);
        } catch (RuntimeException ex) {
            log.error("接口访问日志消息提交失败", ex);
        }
    }
}
