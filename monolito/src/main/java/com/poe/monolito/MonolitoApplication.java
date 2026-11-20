package com.poe.monolito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient // <-- Se registra ante Eureka con el nombre 'monolito'
public class MonolitoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonolitoApplication.class, args);
    }
}