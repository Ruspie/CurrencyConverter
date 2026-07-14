package org.example.config;

import lombok.Getter;
import lombok.Setter;
import org.flywaydb.core.Flyway;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConfigurationProperties(prefix = "spring.flyway")
@Getter
@Setter
public class FlywayConfig {

    private Boolean enabled;
    private String locations;
    private String schemas;
    private Boolean baselineOnMigrate;
    private String baselineVersion;
    private Boolean createSchemas;

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations(locations)
                .baselineOnMigrate(baselineOnMigrate)
                .baselineVersion(baselineVersion)
                .createSchemas(createSchemas)
                .schemas(schemas)
                .load();
    }

}
