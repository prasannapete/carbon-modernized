package com.pcpl.carbon.common_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
// Beans (CRConfig, MapperUtility, GlobalExceptionHandler, ...) and @Entity classes are
// spread across com.pcpl.carbon.common_service, com.pcpl.carbon.commonservice and the
// pcpl-sdk jar (com.pcpl.carbon.pcplsdk.*). Scan/entity-scan the shared com.pcpl.carbon
// root so they are all discovered, as they were before the code was split into modules.
@ComponentScan("com.pcpl.carbon")
@EntityScan("com.pcpl.carbon")
public class CommonServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommonServiceApplication.class, args);
	}

	// LeaderboardsServiceImpl autowires a RestTemplate ("loadBalanced") to call PlayFab
	// REST endpoints via absolute URLs, but no RestTemplate bean was defined anywhere.
	@Bean
	public RestTemplate loadBalanced() {
		return new RestTemplate();
	}

}
