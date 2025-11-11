package io.hpp.noosphere.scheduler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to noosphereScheduler.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }

    // jhipster-needle-application-properties-property-getter

    public static class Liquibase {

        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }
    private final ScheduleTask scheduleTask = new ScheduleTask();
    private final Ethereum ethereum = new Ethereum();


    public ScheduleTask getScheduleTask() {
        return scheduleTask;
    }

    public Ethereum getEthereum() {
        return ethereum;
    }

    public static class ScheduleTask {

        private final CommitmentGeneration commitmentGeneration = new CommitmentGeneration();

        public CommitmentGeneration getCommitmentGeneration() {
            return commitmentGeneration;
        }

        public static class CommitmentGeneration {

            private String cron;

            public String getCron() {
                return cron;
            }

            public void setCron(String cron) {
                this.cron = cron;
            }
        }
    }

    public static class Ethereum {

        private String nodeUrl;

        public String getNodeUrl() {
            return nodeUrl;
        }

        public void setNodeUrl(String nodeUrl) {
            this.nodeUrl = nodeUrl;
        }
    }
}
