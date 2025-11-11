package io.hpp.noosphere.scheduler;

import io.hpp.noosphere.scheduler.config.AsyncSyncConfiguration;
import io.hpp.noosphere.scheduler.config.EmbeddedSQL;
import io.hpp.noosphere.scheduler.config.JacksonConfiguration;
import io.hpp.noosphere.scheduler.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = { NoosphereSchedulerApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class }
)
@EmbeddedSQL
public @interface IntegrationTest {
}
