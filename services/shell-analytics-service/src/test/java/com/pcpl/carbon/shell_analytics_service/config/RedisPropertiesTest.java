package com.pcpl.carbon.shell_analytics_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The Redis connection Spring Boot auto-configures comes from spring.data.redis.*. The deployments
 * provide REDIS_HOST/REDIS_PORT/REDIS_USERNAME/REDIS_PASSWORD, so application.yml must map them;
 * without the mapping the service connects to localhost:6379 and every Redis call fails.
 */
class RedisPropertiesTest {

    private static StandardEnvironment environmentWith(Map<String, Object> envVars) throws IOException {
        StandardEnvironment env = new StandardEnvironment();
        env.getPropertySources().addFirst(new MapPropertySource("deployment-env", envVars));
        for (PropertySource<?> ps : new YamlPropertySourceLoader()
                .load("application.yml", new ClassPathResource("application.yml"))) {
            env.getPropertySources().addLast(ps);
        }
        return env;
    }

    @Test
    void mapsTheDeploymentsRedisVariables() throws IOException {
        StandardEnvironment env = environmentWith(Map.of(
                "REDIS_HOST", "srl-analytics-hold-redis-master",
                "REDIS_PORT", "6380",
                "REDIS_USERNAME", "default",
                "REDIS_PASSWORD", "from-secret"));

        assertEquals("srl-analytics-hold-redis-master", env.getProperty("spring.data.redis.host"));
        assertEquals("6380", env.getProperty("spring.data.redis.port"));
        assertEquals("default", env.getProperty("spring.data.redis.username"));
        assertEquals("from-secret", env.getProperty("spring.data.redis.password"));
    }

    @Test
    void defaultsToALocalRedisWithoutAPasswordForLocalDevelopment() throws IOException {
        StandardEnvironment env = environmentWith(Map.of());

        assertEquals("localhost", env.getProperty("spring.data.redis.host"));
        assertEquals("6379", env.getProperty("spring.data.redis.port"));
        assertEquals("", env.getProperty("spring.data.redis.password"));
    }
}
