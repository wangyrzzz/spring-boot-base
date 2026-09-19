package com.example.demo.mq.provider.rabbit;

import com.example.demo.mq.api.ConsumerRegistration;
import com.example.demo.mq.api.MessageDeliveryType;
import com.example.demo.mq.api.MessageHandler;
import com.example.demo.mq.api.ReceivedMessage;
import com.example.demo.mq.api.MessageTransportType;
import com.example.demo.mq.core.MqConsumeFailureService;
import com.example.demo.mq.spi.MessageQueueProvider;
import com.example.demo.mq.spi.MessageSendCallback;
import com.example.demo.mq.spi.OutboundMessage;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnExpression("'${sys.infra.rabbitmq.enabled:false}' == 'true' && '${spring.rabbitmq.host:}' != '' && '${sys.mq.provider:rabbitmq}' == 'rabbitmq'")
public class RabbitMqProvider implements MessageQueueProvider {

    private final RabbitTemplate rabbitTemplate;
    private final ConnectionFactory connectionFactory;
    private final RabbitPublisherCallbackRegistry callbackRegistry;
    private final MqConsumeFailureService consumeFailureService;
    private final ObjectProvider<AmqpAdmin> amqpAdminProvider;
    private final List<SimpleMessageListenerContainer> containers = new ArrayList<>();

    @Override
    public String name() {
        return "rabbitmq";
    }

    @Override
    public void send(OutboundMessage outboundMessage, MessageSendCallback callback) {
        MessageProperties properties = new MessageProperties();
        properties.setMessageId(outboundMessage.messageId());
        properties.setContentType("application/json");
        properties.setDeliveryMode(org.springframework.amqp.core.MessageDeliveryMode.PERSISTENT);
        properties.setHeader("x-mq-message-id", outboundMessage.messageId());
        properties.setHeader("x-mq-delivery-type", outboundMessage.deliveryType().name());
        outboundMessage.headers().forEach(properties::setHeader);
        Message message = new Message(outboundMessage.body().getBytes(StandardCharsets.UTF_8), properties);
        CorrelationData correlationData = callbackRegistry.register(outboundMessage, callback);
        String routingKey = StringUtils.hasText(outboundMessage.routingKey())
                ? outboundMessage.routingKey() : outboundMessage.destination();
        try {
            rabbitTemplate.send("", routingKey, message, correlationData);
        } catch (RuntimeException ex) {
            callbackRegistry.failed(outboundMessage.messageId(), ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void registerConsumer(ConsumerRegistration registration) {
        AmqpAdmin amqpAdmin = amqpAdminProvider.getIfAvailable();
        if (amqpAdmin != null) {
            amqpAdmin.declareQueue(new Queue(registration.destination(), true));
        }
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(registration.destination());
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setDefaultRequeueRejected(false);
        container.setMissingQueuesFatal(false);
        container.setPrefetchCount(1);
        container.setMessageListener((ChannelAwareMessageListener) (message, channel) ->
                consume(registration, message, channel));
        container.start();
        containers.add(container);
        log.info("RabbitMQ消费者已注册, destination={}, consumerName={}",
                registration.destination(), registration.consumerName());
    }

    private void consume(ConsumerRegistration registration, Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        ReceivedMessage received = toReceivedMessage(registration.destination(), message);
        try {
            registration.handler().handle(received);
            channel.basicAck(deliveryTag, false);
        } catch (Throwable error) {
            try {
                consumeFailureService.record(received, registration.consumerName(), error);
            } catch (Throwable recordError) {
                log.error("消费失败记录落库失败，消息仍将丢弃, destination={}, messageId={}",
                        registration.destination(), received.messageId(), recordError);
            }
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (Exception ackError) {
                log.error("消费失败消息确认丢弃失败, destination={}, messageId={}",
                        registration.destination(), received.messageId(), ackError);
            }
        }
    }

    private ReceivedMessage toReceivedMessage(String destination, Message message) {
        MessageProperties properties = message.getMessageProperties();
        Map<String, String> headers = new HashMap<>();
        properties.getHeaders().forEach((key, value) -> headers.put(key, value == null ? null : String.valueOf(value)));
        String messageId = properties.getMessageId();
        if (!StringUtils.hasText(messageId)) {
            messageId = headers.get("x-mq-message-id");
        }
        MessageDeliveryType type = MessageDeliveryType.NORMAL;
        String typeValue = headers.get("x-mq-delivery-type");
        if (StringUtils.hasText(typeValue)) {
            try {
                type = MessageDeliveryType.valueOf(typeValue);
            } catch (IllegalArgumentException ignored) {
                log.warn("未知消息投递类型，将按普通消息处理, value={}", typeValue);
            }
        }
        return new ReceivedMessage(messageId, destination,
                properties.getReceivedRoutingKey(),
                new String(message.getBody(), StandardCharsets.UTF_8), headers,
                MessageTransportType.RABBITMQ, type);
    }
}
