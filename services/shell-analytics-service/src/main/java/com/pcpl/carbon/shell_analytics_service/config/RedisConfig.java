package com.pcpl.carbon.shell_analytics_service.config;

import com.pcpl.carbon.pcplsdk.ShellAnalytics.ShellEvents.DTO.ShellEventsDataDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, ShellEventsDataDTO> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ShellEventsDataDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(ShellEventsDataDTO.class));
        return template;
    }
}
