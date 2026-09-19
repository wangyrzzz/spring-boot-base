package com.example.demo;

import com.example.demo.es.ConsumerRepository;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import org.redisson.api.RedissonClient;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @Autowired
    private ApplicationContext applicationContext;

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
        assertFalse(applicationContext.getBeansOfType(RedisConnectionFactory.class).containsKey("redisConnectionFactory"));
        assertFalse(applicationContext.getBeansOfType(StringRedisTemplate.class).containsKey("stringRedisTemplate"));
        assertFalse(applicationContext.getBeansOfType(RedissonClient.class).containsKey("redissonClient"));
        assertFalse(applicationContext.getBeansOfType(RabbitTemplate.class).containsKey("rabbitTemplate"));
        assertFalse(applicationContext.getBeansOfType(ElasticsearchOperations.class).containsKey("elasticsearchOperations"));
        assertFalse(applicationContext.getBeansOfType(ConsumerRepository.class).size() > 0);
    }

}
