package com.example.demo.config.infrastructure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Feature switches for optional infrastructure integrations.
 */
@Data
@ConfigurationProperties(prefix = "sys.infra")
public class InfrastructureProperties {

    private Feature redis = new Feature();
    private Feature rabbitmq = new Feature();

    @Data
    public static class Feature {
        private boolean enabled;
    }
}
