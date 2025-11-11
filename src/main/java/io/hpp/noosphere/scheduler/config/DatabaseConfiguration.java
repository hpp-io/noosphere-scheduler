package io.hpp.noosphere.scheduler.config;

import io.hpp.noosphere.scheduler.domain.util.FixedAuditorAware;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class DatabaseConfiguration {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new FixedAuditorAware();
    }
}
