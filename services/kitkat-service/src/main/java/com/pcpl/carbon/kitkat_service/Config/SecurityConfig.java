package com.pcpl.carbon.kitkat_service.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String[] PUBLIC_ENDPOINTS = {
            "/carbon-events/get-all",
            "/carbon-events-scores/get-scores-by-event-id",
            "/carbon-events-scores/export-score-to-excel",
            "/carbon-events/get-leader-board-events",
            "/carbon-events/keep-session",
            "/events/kk-race-played/race-info",
            "/carbon-events/get-all-events",
            // Uploaded logos / generated excel files are fetched by <img> tags and download
            // navigations that carry no Authorization header, so serving must be public.
            "/carbon-events/files/**"
    };

    // The Leaderboard Web's leaderboard data API. Exposed fully publicly (no login)
    // via web.ignoring() below — the same mechanism SRL Dashboard's shell-analytics
    // uses for its public endpoints. This bypasses the JWT filter and CSRF for this
    // path, so the leaderboard loads without a token even if a stale one is present.
    private static final String LEADERBOARD_PUBLIC_ENDPOINT = "/carbon-events/get-leader-board-events";

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(LEADERBOARD_PUBLIC_ENDPOINT);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers(PUBLIC_ENDPOINTS))
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                .anyRequest().authenticated()
                ).oauth2ResourceServer(oauth2 -> {
                    oauth2.jwt(jwt -> jwt
                            .jwtAuthenticationConverter(jwtAuthenticationConverter())
                    );
                });

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}
