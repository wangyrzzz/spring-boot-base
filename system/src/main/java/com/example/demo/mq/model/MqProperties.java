package com.example.demo.mq.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mq")
public class MqProperties {

    private boolean enabled = true;

    private String provider = "rabbitmq";
}
