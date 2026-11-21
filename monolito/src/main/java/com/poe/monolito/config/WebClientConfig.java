package com.poe.monolito.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced // Integra el cliente con Spring Cloud LoadBalancer y Eureka Service Discovery
    public WebClient.builder webClientBuilder() {
        return WebClient.builder();
    }
}