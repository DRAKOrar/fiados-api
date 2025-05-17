package com.fiados_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Permite peticiones desde el origen de Angular
        config.addAllowedOrigin("http://localhost:4200/");

        // Permite los métodos HTTP necesarios
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH"); // Agrega PATCH aquí
        config.addAllowedMethod("OPTIONS");

        // Permite todos los headers
        config.addAllowedHeader("*");

        // Permite enviar cookies (si es necesario)
        config.setAllowCredentials(true);

        // Aplicar la configuración CORS a todas las rutas
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}