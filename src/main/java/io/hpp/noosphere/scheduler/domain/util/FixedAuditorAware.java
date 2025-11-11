package io.hpp.noosphere.scheduler.domain.util;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;

/**
 * Implementation of {@link AuditorAware} that returns a fixed value.
 * This is used for scheduler jobs where there is no human user.
 */
public class FixedAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("system");
    }
}
