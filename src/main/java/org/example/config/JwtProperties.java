package org.example.config;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "app.security.jwt")
@Validated
@Getter
@Setter
public class JwtProperties {

    @NotBlank
    private String accessSecret;
    @NotBlank
    private String refreshSecret;
    @Positive
    private long accessTokenExpiration;
    @Positive
    private long refreshTokenExpiration;

}
