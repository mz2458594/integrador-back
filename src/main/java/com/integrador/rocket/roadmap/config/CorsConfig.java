package com.integrador.rocket.roadmap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
//                COnfigurar con la variable de entorno para el frontend
                .allowedOrigins("http://localhost:5173", "https://integrador-2-frontend.vercel.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
//                "HEAD", "TRACE", "CONNECT"
                )
                .allowCredentials(true)
                .allowedHeaders("*")
        ;
    }

}
