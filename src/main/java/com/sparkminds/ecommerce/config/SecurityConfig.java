package com.sparkminds.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.sparkminds.ecommerce.exception.CustomAccessDeniedHandler;
import com.sparkminds.ecommerce.exception.CustomAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final com.sparkminds.ecommerce.security.CustomJwtConverter customJwtConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .anyRequest().authenticated())

                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtConverter))
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))


                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))


                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
