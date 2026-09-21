package com.pcpl.carbon.kitkat_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
// Scan the shared pcpl-sdk packages so the SDK's @Entity classes (e.g. EventParticipants)
// and the reusable "tenantFilter" @FilterDef are registered; without this the entities
// and the tenant filter would not exist at runtime.
@EntityScan("com.pcpl.carbon")
@ComponentScan(basePackages = {"com.pcpl.carbon"})
@EnableJpaAuditing
@EnableScheduling
public class KitkatServiceApplication {
    @Bean
    public ModelMapper modelMapper() { return new ModelMapper(); }

    @Bean
    public RestTemplate clientAuthenticated() { return new RestTemplate(); }

    // Spring Boot 4's auto-configured JSON mapper is Jackson 3 (tools.jackson.*), so the
    // Jackson 2 com.fasterxml.jackson.databind.ObjectMapper that services autowire is not
    // provided automatically. Define it explicitly.
    @Bean
    public ObjectMapper objectMapper() { return new ObjectMapper(); }

	public static void main(String[] args) {
		SpringApplication.run(KitkatServiceApplication.class, args);
	}

}
