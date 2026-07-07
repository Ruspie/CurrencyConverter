package org.example.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.security.jwt")
@Getter
@Setter
public class JwtProperties {

    private String accessSecret;
    private String refreshSecret;
    private String accessTokenExpiration;
    private String refreshTokenExpiration;

    public long getAccessTokenExpirationMs() {
        return parseToMillis(accessTokenExpiration);
    }

    public long getRefreshTokenExpirationMs() {
        return parseToMillis(refreshTokenExpiration);
    }

    private long parseToMillis(String duration) {
        duration = duration.trim().toLowerCase();
        if (duration.endsWith("ms")) {
            return Long.parseLong(duration.replace("ms", ""));
        } else if (duration.endsWith("s")) {
            return Long.parseLong(duration.replace("s", "")) * 1000;
        } else if (duration.endsWith("m")) {
            return Long.parseLong(duration.replace("m", "")) * 60 * 1000;
        } else if (duration.endsWith("h")) {
            return Long.parseLong(duration.replace("h", "")) * 60 * 60 * 1000;
        } else if (duration.endsWith("d")) {
            return Long.parseLong(duration.replace("d", "")) * 24 * 60 * 60 * 1000;
        } else {
            throw new IllegalArgumentException("Неподдерживаемый формат длительности: " + duration);
        }
    }

}