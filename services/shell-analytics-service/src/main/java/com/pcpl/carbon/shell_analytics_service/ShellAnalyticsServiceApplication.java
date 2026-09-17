package com.pcpl.carbon.shell_analytics_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendgrid.SendGrid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.redisson.api.RedissonClient;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EntityScan("com.pcpl.carbon")
@ComponentScan(basePackages = {"com.pcpl.carbon"})
@EnableJpaAuditing
@EnableScheduling
public class ShellAnalyticsServiceApplication {

    @Bean
    public ModelMapper modelMapper() { return new ModelMapper(); }

    @Bean
    public RestTemplate clientAuthenticated() { return new RestTemplate(); }

    // Spring Boot 4's auto-configured JSON mapper is Jackson 3 (tools.jackson.*), so the
    // Jackson 2 com.fasterxml.jackson.databind.ObjectMapper that services autowire is not
    // provided automatically. Define it explicitly.
    @Bean
    public ObjectMapper objectMapper() { return new ObjectMapper(); }

    // SendGridMailClient autowires a com.sendgrid.SendGrid; build it from the API key
    // (SENDGRID_API_KEY, loaded from the project-root .env).
    @Bean
    public SendGrid sendGrid(@Value("${SENDGRID_API_KEY:}") String apiKey) {
        return new SendGrid(apiKey);
    }

    public static void main(String[] args) {
        SpringApplication.run(ShellAnalyticsServiceApplication.class, args);
    }

}
