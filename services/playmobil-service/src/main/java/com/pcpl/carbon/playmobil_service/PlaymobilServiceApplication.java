package com.pcpl.carbon.playmobil_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
// The @Entity classes and reusable @FilterDef live in the shared pcpl-sdk
// (com.pcpl.carbon.pcplsdk.*), and this module's own controllers/services/repositories
// sit under com.pcpl.carbon.playmobilservice.* — neither is under this app's base
// package (com.pcpl.carbon.playmobil_service), so the default scanning would miss them.
// Scan the whole com.pcpl.carbon root explicitly, mirroring kitkat-service.
@EntityScan("com.pcpl.carbon")
@ComponentScan(basePackages = {"com.pcpl.carbon"})
// Only this module's own repositories — NOT "com.pcpl.carbon", which would also pick up
// the SDK's generic base repository (PCPLCRUDRepository) and fail with
// "Not a managed type: class java.lang.Object".
@EnableJpaRepositories(basePackages = {"com.pcpl.carbon.playmobilservice"})
@EnableJpaAuditing
public class PlaymobilServiceApplication {

    @Bean
    public ModelMapper modelMapper() { return new ModelMapper(); }

    @Bean
    public RestTemplate clientAuthenticated() { return new RestTemplate(); }

    // Spring Boot 4's auto-configured JSON mapper is Jackson 3 (tools.jackson.*), so the
    // Jackson 2 com.fasterxml.jackson.databind.ObjectMapper that the SDK/services autowire
    // is not provided automatically. Define it explicitly.
    @Bean
    public ObjectMapper objectMapper() { return new ObjectMapper(); }

	public static void main(String[] args) {
		SpringApplication.run(PlaymobilServiceApplication.class, args);
	}

}
