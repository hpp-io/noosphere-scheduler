package io.hpp.noosphere.scheduler.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
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
    public Credentials credentials() {
        ApplicationProperties.Chain.Wallet.Keystore keystoreConfig = chainConfig.getWallet().getKeystore();
        String keystorePath = keystoreConfig.getPath();
        String storePassword = keystoreConfig.getPassword();
        String keyAlias = keystoreConfig.getKeys().getEth();

        if (!StringUtils.hasText(keystorePath) || !StringUtils.hasText(storePassword) || !StringUtils.hasText(keyAlias)) {
            throw new IllegalStateException("Keystore path, password, or key alias is not configured properly.");
        }

        try {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            try (InputStream keyStoreStream = new FileInputStream(keystorePath)) {
                keyStore.load(keyStoreStream, storePassword.toCharArray());
            }

            KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(storePassword.toCharArray());
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyAlias, protectionParameter);

            if (privateKeyEntry == null) {
                throw new IllegalStateException("Private key not found in keystore for alias: " + keyAlias);
            }

            PrivateKey privateKey = privateKeyEntry.getPrivateKey();
            if (!(privateKey instanceof ECPrivateKey)) {
                throw new IllegalStateException("The private key in the keystore is not an EC private key.");
            }

            // Extract the BigInteger value from the ECPrivateKey.
            BigInteger privateKeyBigInt = ((ECPrivateKey) privateKey).getS();
            ECKeyPair ecKeyPair = ECKeyPair.create(privateKeyBigInt);
            return Credentials.create(ecKeyPair);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load credentials from keystore", e);
        }
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
