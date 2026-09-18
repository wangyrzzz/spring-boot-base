package com.example.demo;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
class NativeTracingTest {

    @DynamicPropertySource
    static void enableTracing(DynamicPropertyRegistry registry) {
        registry.add("management.tracing.enabled", () -> true);
    }

    @Autowired
    private ObservationRegistry observationRegistry;

    @Autowired
    private AsyncTaskExecutor asyncTaskExecutor;

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void observationPopulatesAndCleansNativeCorrelationIds() {
        Observation observation = Observation.start("native-tracing-test", observationRegistry);
        try (Observation.Scope ignored = observation.openScope()) {
            assertNotNull(MDC.get("traceId"));
            assertNotNull(MDC.get("spanId"));
            assertFalse(MDC.get("traceId").isBlank());
            assertFalse(MDC.get("spanId").isBlank());
        }
        observation.stop();

        assertNull(MDC.get("traceId"));
        assertNull(MDC.get("spanId"));
    }

    @Test
    void asyncExecutorPropagatesObservationContext() throws Exception {
        Observation observation = Observation.start("native-async-tracing-test", observationRegistry);
        String expectedTraceId;
        try (Observation.Scope ignored = observation.openScope()) {
            expectedTraceId = MDC.get("traceId");
            Future<String> future = asyncTaskExecutor.submit(() -> MDC.get("traceId"));
            assertEquals(expectedTraceId, future.get());
        } finally {
            observation.stop();
        }
        assertNull(MDC.get("traceId"));
    }
}
