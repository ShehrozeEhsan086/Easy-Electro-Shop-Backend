package com.easyelectroshop.productservice.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Component;


@Configuration
@Component
public class Config {

    @Bean
    public OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository){
        OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build();
        DefaultOAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository,oAuth2AuthorizedClientRepository);
        defaultOAuth2AuthorizedClientManager.setAuthorizedClientProvider(provider);
        return defaultOAuth2AuthorizedClientManager;
    }
}
