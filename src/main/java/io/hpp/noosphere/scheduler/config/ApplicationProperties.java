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


    public static class ScheduleTask {

        private final CommitmentGeneration commitmentGeneration = new CommitmentGeneration();

        public CommitmentGeneration getCommitmentGeneration() {
            return commitmentGeneration;
        }

        public static class CommitmentGeneration {

            private String enabled;

            private String cron;

            public String getCron() {
                return cron;
            }

            public void setCron(String cron) {
                this.cron = cron;
            }

            public String getEnabled() {
                return enabled;
            }

            public void setEnabled(String enabled) {
                this.enabled = enabled;
            }
        }
    }

    public static class Chain {

        private Boolean enabled;
        private String rpcUrl;
        private Long trailHeadBlocks;
        private String routerAddress;
        private final Wallet wallet = new Wallet();
        private final SnapshotSync snapshotSync = new SnapshotSync();

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getRpcUrl() {
            return rpcUrl;
        }

        public void setRpcUrl(String rpcUrl) {
            this.rpcUrl = rpcUrl;
        }

        public Long getTrailHeadBlocks() {
            return trailHeadBlocks;
        }

        public void setTrailHeadBlocks(Long trailHeadBlocks) {
            this.trailHeadBlocks = trailHeadBlocks;
        }

        public String getRouterAddress() {
            return routerAddress;
        }

        public void setRouterAddress(String routerAddress) {
            this.routerAddress = routerAddress;
        }

        public Wallet getWallet() {
            return wallet;
        }

        public SnapshotSync getSnapshotSync() {
            return snapshotSync;
        }
        public static class Wallet {

            private Long maxGasLimit;
            private String privateKey;
            private String paymentAddress;

            public Long getMaxGasLimit() {
                return maxGasLimit;
            }

            public void setMaxGasLimit(Long maxGasLimit) {
                this.maxGasLimit = maxGasLimit;
            }

            public String getPrivateKey() {
                return privateKey;
            }

            public void setPrivateKey(String privateKey) {
                this.privateKey = privateKey;
            }

            public String getPaymentAddress() {
                return paymentAddress;
            }

            public void setPaymentAddress(String paymentAddress) {
                this.paymentAddress = paymentAddress;
            }
        }

        public static class SnapshotSync {

            private Long sleep;
            private Long batchSize;
            private Long startingSubId;
            private Long syncPeriod;

            public Long getSleep() {
                return sleep;
            }

            public void setSleep(Long sleep) {
                this.sleep = sleep;
            }

            public Long getBatchSize() {
                return batchSize;
            }

            public void setBatchSize(Long batchSize) {
                this.batchSize = batchSize;
            }

            public Long getStartingSubId() {
                return startingSubId;
            }

            public void setStartingSubId(Long startingSubId) {
                this.startingSubId = startingSubId;
            }

            public Long getSyncPeriod() {
                return syncPeriod;
            }

            public void setSyncPeriod(Long syncPeriod) {
                this.syncPeriod = syncPeriod;
            }
        }
    }
}
