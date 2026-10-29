package com.pcpl.carbon.authserver.Config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SecurityConfigOriginTest {

    @Test
    void extractsOriginOfHttpUrls() {
        assertEquals("https://leaderboard.prapticonsulting.com",
                SecurityConfig.originOf("https://leaderboard.prapticonsulting.com/"));
        assertEquals("http://localhost:3001", SecurityConfig.originOf("http://localhost:3001/login?x=1"));
        assertEquals("https://srl-dev-dashboard.prapticonsulting.com",
                SecurityConfig.originOf("HTTPS://SRL-DEV-DASHBOARD.prapticonsulting.com"));
    }

    @Test
    void lookalikeHostsDoNotShareAnOrigin() {
        assertEquals("https://leaderboard.prapticonsulting.com.evil.example",
                SecurityConfig.originOf("https://leaderboard.prapticonsulting.com.evil.example/"));
        assertEquals("http://localhost:30001", SecurityConfig.originOf("http://localhost:30001/"));
    }

    @Test
    void rejectsNonHttpOrRelativeOrUserInfoUrls() {
        assertNull(SecurityConfig.originOf(null));
        assertNull(SecurityConfig.originOf("/relative"));
        assertNull(SecurityConfig.originOf("//evil.example/"));
        assertNull(SecurityConfig.originOf("javascript:alert(1)"));
        assertNull(SecurityConfig.originOf("https://leaderboard.prapticonsulting.com@evil.example/"));
        assertNull(SecurityConfig.originOf("not a url"));
    }
}
