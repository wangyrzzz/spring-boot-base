package com.example.demo.config.infrastructure;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * Validates endpoint configuration only for explicitly enabled integrations.
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(InfrastructureProperties.class)
public class InfrastructureConfiguration implements EnvironmentAware {

    private final InfrastructureProperties properties;
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void validateEnabledEndpoints() {
        if (properties.getRedis().isEnabled()
                && !StringUtils.hasText(environment.getProperty("sys.redisson.address"))) {
            throw new IllegalStateException("sys.infra.redis.enabled=true 时必须配置 sys.redisson.address");
        }
        if (properties.getRabbitmq().isEnabled()
                && !StringUtils.hasText(environment.getProperty("spring.rabbitmq.host"))) {
            throw new IllegalStateException("sys.infra.rabbitmq.enabled=true 时必须配置 spring.rabbitmq.host");
        }
    }
}
