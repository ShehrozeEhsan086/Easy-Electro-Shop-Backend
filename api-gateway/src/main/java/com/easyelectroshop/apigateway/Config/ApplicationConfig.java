package com.easyelectroshop.apigateway.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  @Value("${admin-application-url}")
  private String adminOriginUrl;

  @Value("${customer-application-url}")
  private String customerOriginUrl;

  @Value("${customer-application-url-external}")
  private String customerOriginUrlExternal;

  @Value("${admin-application-url-external}")
  private String adminOriginUrlExternal;

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final var source = new UrlBasedCorsConfigurationSource();
    final var config = new CorsConfiguration();
    final var origins = List.of(customerOriginUrl,adminOriginUrl,customerOriginUrlExternal,adminOriginUrlExternal);
    final var headers = List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE);
    final var methods = List.of(HttpMethod.GET.name(),HttpMethod.POST.name(),HttpMethod.PUT.name(),HttpMethod.DELETE.name());
    final var maxAge = Duration.ofSeconds(86400);

    config.setAllowedOrigins(origins);
    config.setAllowedHeaders(headers);
    config.setAllowedMethods(methods);
    config.setMaxAge(maxAge);

    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public CorsWebFilter corsWebFilter() {
    final var configSource = this.corsConfigurationSource();
    return new CorsWebFilter(configSource);
  }
}
