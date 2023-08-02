package com.easyelectroshop.productservice.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CorsSpec;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


  private final OAuth2ResourceServerProperties resourceServerProps;

  @Value("${application.audience}")
  private String audience;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
    httpSecurity
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers("/api/v1/product/management/**")
            .authenticated()
            .requestMatchers("/api/v1/product/**")
            .permitAll()
            .requestMatchers("/actuator/**")
            .permitAll();
    return httpSecurity.build();
  }


  private ReactiveJwtDecoder makeJwtDecoder() {
    final var issuer = resourceServerProps.getJwt().getIssuerUri();
    final var genericDecoder = ReactiveJwtDecoders.fromIssuerLocation(issuer);

    if (genericDecoder instanceof final NimbusReactiveJwtDecoder decoder) {
      final var withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
      final var tokenValidator = new DelegatingOAuth2TokenValidator<>(withIssuer, this::withAudience);

      decoder.setJwtValidator(tokenValidator);
      return decoder;
    }

    return genericDecoder;
  }

  private OAuth2TokenValidatorResult withAudience(final Jwt token) {
    final var audienceError = new OAuth2Error(
      OAuth2ErrorCodes.INVALID_TOKEN,
      "The token was not issued for the given audience",
      "https://datatracker.ietf.org/doc/html/rfc6750#section-3.1"
    );

    return token.getAudience().contains(audience)
      ? OAuth2TokenValidatorResult.success()
      : OAuth2TokenValidatorResult.failure(audienceError);
  }
}
