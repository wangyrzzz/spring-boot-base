package com.example.demo;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "management.tracing.sampling.probability=1.0",
        "management.server.port=0"
})
@ActiveProfiles("test")
@Import(NativeTracingHttpTest.TraceEndpointConfiguration.class)
class NativeTracingHttpTest {

    private static final String TRACE_ID = "4bf92f3577b34da6a3ce929d0e0e4736";
    private static final String PARENT_SPAN_ID = "00f067aa0ba902b7";

    @LocalServerPort
    private int port;

    @Autowired
    private Propagator propagator;

    @DynamicPropertySource
    static void enableTracing(DynamicPropertyRegistry registry) {
        registry.add("management.tracing.enabled", () -> true);
    }

    @Test
    void configuresNativeW3cPropagator() {
        assertTrue(propagator.fields().contains("traceparent"));
    }

    @Test
    void extractsW3cTraceparentWithoutLegacyHeader() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/actuator/trace-test"))
                .header("traceparent", "00-" + TRACE_ID + "-" + PARENT_SPAN_ID + "-01")
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(TRACE_ID), response.body());
        assertNull(response.headers().firstValue("X-Trace-Id").orElse(null));
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TraceEndpointConfiguration {
        @Bean
        TraceEndpoint traceEndpoint(Tracer tracer) {
            return new TraceEndpoint(tracer);
        }
    }

    @RestController
    static class TraceEndpoint {
        private final Tracer tracer;

        TraceEndpoint(Tracer tracer) {
            this.tracer = tracer;
        }

        @GetMapping("/actuator/trace-test")
        Map<String, String> currentTrace() {
            return Map.of(
                    "traceId", tracer.currentSpan().context().traceId(),
                    "mdcTraceId", MDC.get("traceId"));
        }
    }
}
