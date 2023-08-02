package com.easyelectroshop.productservice;

import com.easyelectroshop.productservice.DTO.ProductDTO.Product;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductServiceApplication {

    @Value("${internal.communication.header.name}")
    private String headerName;

    @Value("${internal.communication.header.value}")
    private String headerValue;

//    @Autowired
//    OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }


    @Bean
    @LoadBalanced
    WebClient.Builder getWebClientBuilder(){
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        return WebClient.builder()
                .exchangeStrategies(strategies)
                .filter((clientRequest, next) -> {
                    String authorizationHeader = clientRequest.headers().getFirst(HttpHeaders.AUTHORIZATION);

                    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                        ClientRequest authorizedRequest = ClientRequest.from(clientRequest)
                                .headers(headers -> {
                                    headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
                                    headers.set(headerName, headerValue);
                                })
                                .build();
                        return next.exchange(authorizedRequest);
                    } else {
                        ClientRequest internallyAuthorizedRequest = ClientRequest.from(clientRequest)
                                .headers(headers -> headers.set(headerName, headerValue))
                                .build();
                        return next.exchange(internallyAuthorizedRequest);
                    }
                });
    }
    @Bean
    MultipartBodyBuilder multipartBodyBuilder(){
        return new MultipartBodyBuilder();
    }

    // Object sent as a fallback for failed API call to Product Management Service
    @Bean
    Product productFallbackObj(){
        return new Product(UUID.fromString("00000000-0000-0000-0000-000000000000"),"Default Product", null,"Short Description","Complete Description",
                " ","Default",0,false,0,0,1,"0",null,0,null," ",
                " ",true,null,null);
    }

}
