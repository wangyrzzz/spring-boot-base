package com.example.demo.config;

import com.example.demo.mq.model.MqProperties;
import com.example.demo.mq.model.MqRetryProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({MqProperties.class, MqRetryProperties.class})
public class MqConfiguration {
}
