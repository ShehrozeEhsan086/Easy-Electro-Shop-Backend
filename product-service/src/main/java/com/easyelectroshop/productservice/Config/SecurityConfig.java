package com.easyelectroshop.productservice.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.authorizeHttpRequests().anyRequest().permitAll();
//        httpSecurity.authorizeHttpRequests()
//                .requestMatchers("/api/v1/product/management/**")
//                .authenticated()
//                .requestMatchers("/actuator/**").permitAll()
//                .requestMatchers("/api/v1/product/**")
//                .permitAll()
//                .and()
//                .oauth2ResourceServer()
//                .jwt();
        return httpSecurity.build();
    }
}
