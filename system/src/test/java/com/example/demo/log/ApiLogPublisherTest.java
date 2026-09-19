package com.example.demo.log;

import com.example.demo.entity.SysLogApi;
import com.example.demo.mq.api.MessageQueueTemplate;
import com.example.demo.mq.api.MessageRequest;
import com.example.demo.mq.api.MessageTransportType;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ApiLogPublisherTest {

    @Test
    void publishesApiLogThroughSpringEventTransport() {
        MessageQueueTemplate template = mock(MessageQueueTemplate.class);
        ApiLogPublisher publisher = new ApiLogPublisher(template);

        publisher.publish(new SysLogApi());

        verify(template).send(any(MessageRequest.class), eq(MessageTransportType.SPRING_EVENT));
    }
}
