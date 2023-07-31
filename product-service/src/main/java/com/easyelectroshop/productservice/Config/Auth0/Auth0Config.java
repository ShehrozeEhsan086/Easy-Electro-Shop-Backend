package com.easyelectroshop.productservice.Config.Auth0;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Configuration
@ConfigurationProperties(prefix = "auth0")
public class Auth0Config {

//    @Value("${userBucket.path}")
//    private String userBucketPath;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new ClientRegistrationRepository() {
            @Override
            public ClientRegistration findByRegistrationId(String registrationId) {
                if ("myInternalClient".equals(registrationId)) {
                    return ClientRegistration.withRegistrationId("myInternalClient")
                            .clientId("uIET7bX8Ff4jh2JpHZYovnKxjKQ1ZZSf")
                            .clientSecret("oZhQ7dvqk2FbcK37C2kfwFT27cWeQaNk00Ekq7Cj4EyQjNYw-0Rp9u1eYuVDtAJp")
                            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                            .scope("internal")
                            .tokenUri("https://dev-hb635mdqnobuyyhh.us.auth0.com/")
                            .build();
                }
                return null;
            }
        };
    }
}