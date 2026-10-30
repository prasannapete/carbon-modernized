package com.pcpl.carbon.shell_analytics_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/srl/get-access-token/**","/user/reset-new-password");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Allow the SRL Dashboard SPA (localhost:3000) to call the API cross-origin
                // with its Bearer token; without this the pre-flight OPTIONS is rejected (401).
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests((authorizeRequests) ->
                authorizeRequests
                        .requestMatchers( "/srl/get-access-token/** ","/user/reset-new-password").permitAll()
                        .anyRequest().authenticated()
        ).oauth2ResourceServer(oauth2 -> {
            oauth2.jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
            );
        });

        return http.build();
    }

    /**
     * CORS for the SRL Dashboard SPA, which calls this service from the browser
     * (cross-origin) with the auth server's Bearer token. The allowed origins come from
     * {@code com.pcpl.carbon.allowedOrigins} (AUTH_ALLOWED_ORIGINS, as for the auth server),
     * so each environment lists its own dashboard origin.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${com.pcpl.carbon.allowedOrigins}") List<String> allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins.stream()
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList());
        configuration.setAllowedMethods(List.of("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Content-Disposition"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}
