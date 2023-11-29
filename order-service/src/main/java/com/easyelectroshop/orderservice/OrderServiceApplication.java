package com.easyelectroshop.orderservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.DecimalFormat;
import java.time.LocalDate;

@SpringBootApplication
@EnableScheduling
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Value("${internal.communication.header.name}")
    private String headerName;

    @Value("${internal.communication.header.value}")
    private String headerValue;

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
    public DecimalFormat decimalFormat() {
        return new DecimalFormat("####");
    }

}