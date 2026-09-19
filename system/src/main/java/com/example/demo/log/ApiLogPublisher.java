package com.example.demo.log;

import com.example.demo.common.Constant;
import com.example.demo.entity.SysLogApi;
import com.example.demo.mq.api.MessageQueueTemplate;
import com.example.demo.mq.api.MessageRequest;
import com.example.demo.mq.api.MessageTransportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiLogPublisher {
    private final MessageQueueTemplate messageQueueTemplate;

    public void publish(SysLogApi apiLog) {
        try {
            messageQueueTemplate.send(MessageRequest.of(Constant.API_LOG_QUEUE, apiLog),
                    MessageTransportType.SPRING_EVENT);
        } catch (RuntimeException ex) {
            log.error("接口访问日志Spring Event提交失败", ex);
        }
    }
}
