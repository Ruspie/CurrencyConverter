package org.example.service.impl;

import org.example.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setAccessSecret(secret("access"));
        properties.setRefreshSecret(secret("refresh"));
        properties.setAccessTokenExpiration(60_000);
        properties.setRefreshTokenExpiration(120_000);
        jwtService = new JwtServiceImpl(properties);
    }

    @Test
    void generatesAndReadsAccessToken() {
        String token = jwtService.generateAccessToken("user", List.of("ROLE_USER"));

        assertThat(jwtService.validateAccessToken(token)).isTrue();
        assertThat(jwtService.getUsernameFromAccessToken(token)).isEqualTo("user");
        assertThat(jwtService.getRolesFromAccessToken(token)).containsExactly("ROLE_USER");
    }

    @Test
    void usesSeparateRefreshSecret() {
        String token = jwtService.generateRefreshToken("user");

        assertThat(jwtService.validateRefreshToken(token)).isTrue();
        assertThat(jwtService.validateAccessToken(token)).isFalse();
        assertThat(jwtService.validateRefreshToken("not-a-jwt")).isFalse();
    }

    private String secret(String prefix) {
        String value = prefix + "-secret-key-that-is-at-least-32-bytes-long";
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
