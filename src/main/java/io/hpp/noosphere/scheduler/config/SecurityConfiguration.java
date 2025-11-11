package io.hpp.noosphere.scheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않음
            .authorizeHttpRequests(authz ->
                authz
                    // 1. Consul Health Check를 위한 엔드포인트는 모두 허용
                    .requestMatchers(HttpMethod.GET, "/management/health", "/management/health/**")
                    .permitAll()
                    // 2. 그 외 모든 /management/** 경로는 거부 (또는 특정 IP만 허용)
                    .requestMatchers("/management/**")
                    .denyAll()
                    // 3. 나머지 모든 요청도 기본적으로 거부 (가장 안전한 기본값)
                    .anyRequest()
                    .denyAll()
            );
        return http.build();
    }
}
