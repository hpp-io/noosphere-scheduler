package io.hpp.noosphere.scheduler.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Properties specific to noosphereScheduler.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();
    private final ScheduleTask scheduleTask = new ScheduleTask();
    private final Chain chain = new Chain();


    public Liquibase getLiquibase() {
        return liquibase;
    }

    public ScheduleTask getScheduleTask() {
        return scheduleTask;
    }

    public Chain getChain() {
        return chain;
    }


    public static class Liquibase {

        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }


    @Getter
    @Setter
    public static class ScheduleTask {

        private final CommitmentGeneration commitmentGeneration = new CommitmentGeneration();

        @Setter
        @Getter
        public static class CommitmentGeneration {
            private String enabled;
            private String cron;

        }
    }

    @Getter
    @Setter
    public static class Chain {

        private Boolean enabled;
        private String rpcUrl;
        private Long trailHeadBlocks;
        private String routerAddress;
        private final Connection connection = new Connection();
        private final Wallet wallet = new Wallet();
        private final SnapshotSync snapshotSync = new SnapshotSync();
        private final GasConfig gasConfig  = new GasConfig();

        @Setter
        @Getter
        public static class Connection {
            private Integer timeout;
            private Integer readTimeout;
            private Integer writeTimeout;
        }

        @Setter
        @Getter
        public static class Wallet {

            private Long maxGasLimit;
            private String privateKey;
            private String paymentAddress;
            @NotNull
            private List<String> allowedSimErrors = new ArrayList<>();
        }

        @Setter
        @Getter
        public static class SnapshotSync {

            private Long sleep;
            private Long batchSize;
            private Long startingSubId;
            private Long syncPeriod;

        }

        @Setter
        @Getter
        public static class GasConfig {
            private Double priceMultiplier;
            private Double limitMultiplier;
        }
    }
}
