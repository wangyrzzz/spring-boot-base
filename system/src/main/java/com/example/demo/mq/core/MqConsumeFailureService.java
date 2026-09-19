package com.example.demo.mq.core;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.mapper.MqConsumeFailureMapper;
import com.example.demo.mq.api.ReceivedMessage;
import com.example.demo.mq.model.MqConsumeFailure;
import com.example.demo.mq.model.MqConsumeFailureStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MqConsumeFailureService extends ServiceImpl<MqConsumeFailureMapper, MqConsumeFailure> {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public MqConsumeFailure record(ReceivedMessage message, String consumerName, Throwable error) {
        MqConsumeFailure failure = new MqConsumeFailure();
        failure.setProvider(message.transportType() == null
                ? "rabbitmq" : message.transportType().name().toLowerCase(Locale.ROOT));
        failure.setSourceMessageId(message.messageId());
        failure.setDeliveryType(message.deliveryType() == null ? null : message.deliveryType().name());
        failure.setDestination(message.destination());
        failure.setRoutingKey(message.routingKey());
        failure.setConsumerName(consumerName);
        failure.setPayload(message.body());
        failure.setHeaders(message.headers() == null ? "{}" : JSON.toJSONString(message.headers()));
        failure.setExceptionType(error == null ? null : error.getClass().getName());
        failure.setErrorMessage(error == null ? "消费处理失败" : truncate(error.getMessage()));
        failure.setStackTrace(stackTrace(error));
        failure.setStatus(MqConsumeFailureStatus.PENDING_MANUAL.getValue());
        failure.setRetryCount(0);
        failure.setCreateTime(new Date());
        failure.setUpdateTime(new Date());
        save(failure);
        return failure;
    }

    @Transactional
    public boolean markRetrySubmitted(Long id, String newMessageId) {
        MqConsumeFailure failure = getById(id);
        if (failure == null || MqConsumeFailureStatus.RESOLVED.getValue().equals(failure.getStatus())) {
            return false;
        }
        failure.setStatus(MqConsumeFailureStatus.RETRY_SUBMITTED.getValue());
        failure.setRetryCount((failure.getRetryCount() == null ? 0 : failure.getRetryCount()) + 1);
        failure.setLastRetryMessageId(newMessageId);
        failure.setUpdateTime(new Date());
        return updateById(failure);
    }

    @Transactional
    public boolean resolve(Long id, Long operatorId, String remark) {
        MqConsumeFailure failure = getById(id);
        if (failure == null) {
            return false;
        }
        failure.setStatus(MqConsumeFailureStatus.RESOLVED.getValue());
        failure.setHandledBy(operatorId);
        failure.setHandledTime(new Date());
        failure.setHandleRemark(remark);
        failure.setUpdateTime(new Date());
        return updateById(failure);
    }

    public Page<MqConsumeFailure> page(long current, long size, Integer status,
                                       String destination, String consumerName) {
        LambdaQueryWrapper<MqConsumeFailure> wrapper = new LambdaQueryWrapper<MqConsumeFailure>()
                .eq(status != null, MqConsumeFailure::getStatus, status)
                .like(StringUtils.hasText(destination), MqConsumeFailure::getDestination, destination)
                .eq(StringUtils.hasText(consumerName), MqConsumeFailure::getConsumerName, consumerName)
                .orderByDesc(MqConsumeFailure::getCreateTime)
                .orderByDesc(MqConsumeFailure::getId);
        return page(new Page<>(current, size), wrapper);
    }

    private String stackTrace(Throwable error) {
        if (error == null) {
            return null;
        }
        StringWriter writer = new StringWriter();
        error.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    private String truncate(String value) {
        if (!StringUtils.hasText(value)) {
            return "消费处理失败";
        }
        return value.length() <= 4000 ? value : value.substring(0, 4000);
    }
}
