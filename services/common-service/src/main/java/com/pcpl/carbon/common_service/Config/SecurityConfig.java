package com.pcpl.carbon.commonservice.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Only the public leaderboard read endpoint is exempt from CSRF; every
                // other request keeps the default CSRF handling.
                .csrf(csrf -> csrf.ignoringRequestMatchers("/leaderboard/get"))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        // Public: allow the leaderboard read without authentication.
                        .requestMatchers(HttpMethod.POST, "/leaderboard/get").permitAll()
                        // Everything else keeps the existing JWT-based authentication.
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
