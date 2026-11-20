package com.poe.gateway.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE) // Máxima prioridad: Intercepta las peticiones OPTIONS antes de enrutarlas
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir envío de cookies y credenciales si fuera necesario
        config.setAllowCredentials(true);
        
        // Orígenes permitidos: Vite (5173) y Create React App (3000)
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        
        // Permitir todas las cabeceras HTTP
        config.setAllowedHeaders(List.of("*"));
        
        // Métodos HTTP soportados en nuestra API REST
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}