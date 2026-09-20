package com.integrador.rocket.roadmap.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
//                COnfigurar con la variable de entorno para el frontend
//                .allowedOrigins()
                .allowedMethods("GET", "POST", "PUT", "DELETE"
//                       , "OPTIONS", "HEAD", "TRACE", "CONNECT"
                );
    }

}
