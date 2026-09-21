package com.example.demo.config.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Prevents Spring Boot from creating clients for integrations that are disabled.
 */
public class InfrastructureAutoConfigurationExclusionEnvironmentPostProcessor
        implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE = "infrastructureAutoConfigurationExclusions";

    private static final String[] REDIS_AUTO_CONFIGURATIONS = {
            "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
    };

    private static final String[] RABBITMQ_AUTO_CONFIGURATIONS = {
            "org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration"
    };

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Set<String> exclusions = new LinkedHashSet<>();
        String configured = environment.getProperty("spring.autoconfigure.exclude");
        if (StringUtils.hasText(configured)) {
            exclusions.addAll(Arrays.stream(configured.split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .toList());
        }
        if (!environment.getProperty("sys.infra.redis.enabled", Boolean.class, false)) {
            exclusions.addAll(Arrays.asList(REDIS_AUTO_CONFIGURATIONS));
        }
        if (!environment.getProperty("sys.infra.rabbitmq.enabled", Boolean.class, false)) {
            exclusions.addAll(Arrays.asList(RABBITMQ_AUTO_CONFIGURATIONS));
        }
        if (exclusions.isEmpty()) {
            return;
        }
        environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE,
                java.util.Map.of("spring.autoconfigure.exclude", String.join(",", exclusions))));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
