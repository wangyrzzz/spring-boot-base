package com.example.demo.mq.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sys.mq.retry")
public class MqRetryProperties {

    private long fixedDelayMs = 180_000L;

    private int maxAttempts = 10;

    private int batchSize = 100;

    private long staleSendingTimeoutMs = 600_000L;
}
