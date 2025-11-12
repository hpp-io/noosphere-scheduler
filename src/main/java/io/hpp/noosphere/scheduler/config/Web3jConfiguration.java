package io.hpp.noosphere.scheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * Configuration class for Web3j.
 */
@Configuration
public class Web3jConfiguration {

    @Value("${application.chain.rpcUrl}")
    private String rpcUrl;

    @Bean
    public Web3j web3j() {
        // HttpService를 사용하여 Web3j 인스턴스를 생성하고 빈으로 등록합니다.
        return Web3j.build(new HttpService(rpcUrl));
    }
}
