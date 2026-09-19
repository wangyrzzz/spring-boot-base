package com.example.demo.mq;

import com.example.demo.mq.api.MessageDeliveryType;
import com.example.demo.mq.provider.rabbit.RabbitPublisherCallbackRegistry;
import com.example.demo.mq.spi.MessageSendCallback;
import com.example.demo.mq.spi.OutboundMessage;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RabbitPublisherCallbackRegistryTest {

    @Test
    void confirmAckNotifiesSuccess() {
        RabbitPublisherCallbackRegistry registry = new RabbitPublisherCallbackRegistry();
        List<String> events = new CopyOnWriteArrayList<>();
        OutboundMessage message = message("message-1");
        CorrelationData correlationData = registry.register(message, callback(events));

        registry.confirm(correlationData, true, null);

        assertEquals(List.of("success:message-1"), events);
    }

    @Test
    void returnedMessageNotifiesFailureAndLaterConfirmDoesNotDuplicateFailure() {
        RabbitPublisherCallbackRegistry registry = new RabbitPublisherCallbackRegistry();
        List<String> events = new CopyOnWriteArrayList<>();
        OutboundMessage message = message("message-2");
        CorrelationData correlationData = registry.register(message, callback(events));
        MessageProperties properties = new MessageProperties();
        properties.setMessageId(message.messageId());
        ReturnedMessage returned = new ReturnedMessage(
                new Message("body".getBytes(StandardCharsets.UTF_8), properties),
                312, "NO_ROUTE", "", message.destination());

        registry.returned(returned);
        registry.confirm(correlationData, true, null);

        assertEquals(1, events.size());
        assertTrue(events.get(0).startsWith("failure:message-2:"));
    }

    private MessageSendCallback callback(List<String> events) {
        return new MessageSendCallback() {
            @Override
            public void onSuccess(String messageId) {
                events.add("success:" + messageId);
            }

            @Override
            public void onFailure(String messageId, String reason) {
                events.add("failure:" + messageId + ":" + reason);
            }
        };
    }

    private OutboundMessage message(String messageId) {
        return new OutboundMessage(messageId, "queue", null, "{}", Map.of(), MessageDeliveryType.NORMAL);
    }
}
