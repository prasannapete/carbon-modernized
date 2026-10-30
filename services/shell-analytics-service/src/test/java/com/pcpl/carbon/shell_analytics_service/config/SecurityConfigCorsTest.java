package com.pcpl.carbon.shell_analytics_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SecurityConfigCorsTest {

    private static CorsConfiguration corsFor(List<String> allowedOrigins) {
        CorsConfiguration config = new SecurityConfig()
                .corsConfigurationSource(allowedOrigins)
                .getCorsConfiguration(new MockHttpServletRequest("POST", "/srl/country/get-countries-by-user"));
        assertNotNull(config, "a CORS configuration must be registered for every path");
        return config;
    }

    @Test
    void allowsTheConfiguredDashboardOrigin() {
        CorsConfiguration config = corsFor(List.of(
                "http://localhost:3000", "https://srl-dev-dashboard.prapticonsulting.com"));

        assertEquals("https://srl-dev-dashboard.prapticonsulting.com",
                config.checkOrigin("https://srl-dev-dashboard.prapticonsulting.com"));
        assertEquals("http://localhost:3000", config.checkOrigin("http://localhost:3000"));
    }

    @Test
    void rejectsOriginsThatAreNotConfigured() {
        CorsConfiguration config = corsFor(List.of("https://srl-dev-dashboard.prapticonsulting.com"));

        assertNull(config.checkOrigin("https://evil.example"));
        assertNull(config.checkOrigin("https://srl-dev-dashboard.prapticonsulting.com.evil.example"));
        assertNull(config.checkOrigin("http://localhost:3000"));
    }

    @Test
    void ignoresBlanksAndSurroundingSpacesInTheConfiguredList() {
        CorsConfiguration config = corsFor(List.of(" https://srl-dashboard.carbon12011ar.com ", "", "  "));

        assertEquals("https://srl-dashboard.carbon12011ar.com",
                config.checkOrigin("https://srl-dashboard.carbon12011ar.com"));
        assertEquals(List.of("https://srl-dashboard.carbon12011ar.com"), config.getAllowedOrigins());
    }

    @Test
    void keepsCredentialsAndTheDashboardsHeaders() {
        CorsConfiguration config = corsFor(List.of("https://srl-dev-dashboard.prapticonsulting.com"));

        assertEquals(Boolean.TRUE, config.getAllowCredentials());
        assertEquals(List.of("Authorization", "Content-Type"), config.getAllowedHeaders());
        assertEquals(List.of("Content-Disposition"), config.getExposedHeaders());
    }
}
