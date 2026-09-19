package com.example.demo.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.mq.api.MessageQueueTemplate;
import com.example.demo.mq.api.MessageRequest;
import com.example.demo.mq.core.DefaultMessageQueueTemplate;
import com.example.demo.mq.core.MqSendMessageService;
import com.example.demo.mq.spi.MessageQueueProvider;
import com.example.demo.mq.spi.MessageSendCallback;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultMessageQueueTemplateTest {

    @Test
    void normalSendRoutesProviderFailureToPersistentService() {
        ObjectProvider<MessageQueueProvider> providerProvider = mock(ObjectProvider.class);
        MessageQueueProvider provider = mock(MessageQueueProvider.class);
        MqSendMessageService service = mock(MqSendMessageService.class);
        when(providerProvider.getIfAvailable()).thenReturn(provider);
        DefaultMessageQueueTemplate template = new DefaultMessageQueueTemplate(
                providerProvider, new ObjectMapper(), service);

        doAnswer(invocation -> {
            MessageSendCallback callback = invocation.getArgument(1);
            callback.onFailure(invocation.<com.example.demo.mq.spi.OutboundMessage>getArgument(0).messageId(), "broker down");
            return null;
        }).when(provider).send(any(), any());

        String messageId = template.send(MessageRequest.of("queue", Map.of("id", 1)));

        verify(service).markFailure(any(), eq("broker down"));
        verify(provider).send(any(), any());
        assertNotNull(messageId);
    }

    @Test
    void normalSendWithoutProviderIsRecordedAsFailure() {
        ObjectProvider<MessageQueueProvider> providerProvider = mock(ObjectProvider.class);
        MqSendMessageService service = mock(MqSendMessageService.class);
        when(providerProvider.getIfAvailable()).thenReturn(null);
        MessageQueueTemplate template = new DefaultMessageQueueTemplate(
                providerProvider, new ObjectMapper(), service);

        template.send(MessageRequest.of("queue", "payload"));

        verify(service).markFailure(any(), eq("没有可用的消息队列提供者"));
    }
}
