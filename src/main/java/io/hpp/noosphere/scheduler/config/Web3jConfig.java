package io.hpp.noosphere.scheduler.config;

import io.hpp.noosphere.scheduler.service.KeystoreService;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import static io.hpp.noosphere.scheduler.config.Constants.KEYSTORE_TYPE;

@Configuration
public class Web3jConfig {

    private final ApplicationProperties.Chain chainConfig;

    // Default timeout values
    private static final int DEFAULT_CONNECT_TIMEOUT = 30000; // 30 seconds
    private static final int DEFAULT_READ_TIMEOUT = 30000; // 30 seconds
    private static final int DEFAULT_WRITE_TIMEOUT = 30000; // 30 seconds

    public Web3jConfig(ApplicationProperties applicationProperties) {
        this.chainConfig = applicationProperties.getChain();
    }

    @Bean
    public Web3j web3j() {
        String rpcUrl = chainConfig.getRpcUrl();

        // Get timeout values in a null-safe way
        ApplicationProperties.Chain.Connection connection = chainConfig.getConnection();

        int connectTimeout = getTimeoutValue(connection != null ? connection.getTimeout() : null, DEFAULT_CONNECT_TIMEOUT);
        int readTimeout = getTimeoutValue(connection != null ? connection.getReadTimeout() : null, DEFAULT_READ_TIMEOUT);
        int writeTimeout = getTimeoutValue(connection != null ? connection.getWriteTimeout() : null, DEFAULT_WRITE_TIMEOUT);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);

        HttpService httpService = new HttpService(rpcUrl, clientBuilder.build());
        return Web3j.build(httpService);
    }

    /**
     * Helper method to return a default value if the timeout is null.
     */
    private int getTimeoutValue(Integer timeout, int defaultValue) {
        return timeout != null ? timeout : defaultValue;
    }

    @Bean
    public Credentials credentials(KeystoreService keystoreService) {
        String keyAlias = chainConfig.getWallet().getKeystore().getKeys().getEth();
        if (keyAlias == null || keyAlias.isBlank()) {
            throw new IllegalStateException("Ethereum key alias 'application.chain.wallet.keystore.keys.eth' is not configured.");
        }
        // Delegate credential loading to the centralized KeystoreService
        return keystoreService.getCredentials(keyAlias);
    }

    @Bean
    public CustomGasProvider gasProvider() {
        return new CustomGasProvider(chainConfig.getGasConfig());
    }

    public static class CustomGasProvider extends DefaultGasProvider {

        private final ApplicationProperties.Chain.GasConfig gasConfig;

        public CustomGasProvider(ApplicationProperties.Chain.GasConfig gasConfig) {
            this.gasConfig = gasConfig;
        }

        @Override
        public BigInteger getGasPrice(String contractFunc) {
            BigInteger basePrice = super.getGasPrice(contractFunc);
            return basePrice.multiply(BigInteger.valueOf((long) (gasConfig.getPriceMultiplier() * 100))).divide(BigInteger.valueOf(100));
        }

        @Override
        public BigInteger getGasLimit(String contractFunc) {
            BigInteger baseLimit = super.getGasLimit(contractFunc);
            return baseLimit.multiply(BigInteger.valueOf((long) (gasConfig.getLimitMultiplier() * 100))).divide(BigInteger.valueOf(100));
        }
    }

    @Bean
    public BigInteger chainId(Web3j web3j) {
        try {
            return web3j.ethChainId().send().getChainId();
        } catch (IOException e) {
            throw new RuntimeException("Failed to get chain ID", e);
        }
    }
}
