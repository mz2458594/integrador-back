package com.integrador.rocket.roadmap.infra.security;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    private static final Logger log = LoggerFactory.getLogger("SECURITY_AUDIT");


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .logout(logout -> logout.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> {
                    request.requestMatchers(HttpMethod.POST, "/login").permitAll();
                    request.requestMatchers(HttpMethod.POST, "/logout").permitAll();
                    request.requestMatchers(HttpMethod.POST, "/register").permitAll();
                    request.requestMatchers("/actuator/health").permitAll();
                    request.requestMatchers(HttpMethod.POST, "/vocational-question").hasRole("ADMINISTRADOR");
                    request.requestMatchers(HttpMethod.POST, "/vocational-option").hasRole("ADMINISTRADOR");
                    request.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    request.requestMatchers("/v3/api-docs/**", "/swagger-ui.html/**", "/swagger-ui/**", "/ws/**").permitAll();
                    request.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        ex ->
                                ex.authenticationEntryPoint(((request, response, authException) -> {
                                    log.warn("UNAUTHENTICATED ip={} {} {}",
                                            request.getRemoteAddr(), request.getMethod(), clean(request.getRequestURI())
                                            );
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                }))
                                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                                            var auth = SecurityContextHolder.getContext().getAuthentication();
                                            log.warn("ACCESS DENIED user={} ip={} {} {}",
                                                    auth != null ? auth.getName() : "anonimo",
                                                    request.getRemoteAddr(), request.getMethod(), clean(request.getRequestURI())
                                                    );
                                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                        })
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    private String clean (String value){
        return value == null ? "null": value.replaceAll("[\\r\\n]", "_");
    }


}
