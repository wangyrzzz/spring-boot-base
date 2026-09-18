package com.example.demo;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests {

    @DynamicPropertySource
    static void enableTracing(DynamicPropertyRegistry registry) {
        registry.add("management.tracing.enabled", () -> true);
    }

    @Autowired
    private Environment environment;

    @Autowired
    private ObservationRegistry observationRegistry;

    @Autowired
    private Tracer tracer;

    @Test
    void contextLoads() {
        assertNotNull(observationRegistry);
        assertNotNull(tracer);
        assertEquals("spring-boot-base", environment.getProperty("spring.application.name"));
        assertEquals("8081", environment.getProperty("management.server.port"));
        assertEquals("health,info,metrics,prometheus",
                environment.getProperty("management.endpoints.web.exposure.include"));
        assertEquals("w3c", environment.getProperty("management.tracing.propagation.type"));
        assertEquals("true", environment.getProperty("management.tracing.enabled"));
        assertEquals("0.1", environment.getProperty("management.tracing.sampling.probability"));
        assertEquals("false", environment.getProperty("management.otlp.tracing.export.enabled"));
        assertEquals("true", environment.getProperty("spring.rabbitmq.template.observation-enabled"));
        assertEquals("true", environment.getProperty("spring.rabbitmq.listener.simple.observation-enabled"));
    }

}
