package com.easyelectroshop.productservice;

import com.easyelectroshop.productservice.DTO.ProductDTO.Product;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductServiceApplication {

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
                .exchangeStrategies(strategies);
    }

    @Bean
    MultipartBodyBuilder multipartBodyBuilder(){
        return new MultipartBodyBuilder();
    }

    // Object send as a fallback for failed API call to Product Management Service
    @Bean
    Product productFallback(){
        return new Product(UUID.fromString("00000000-0000-0000-0000-000000000000"),"Default Product", null,"Short Description","Complete Description",
                " ","Default",0,false,0,0,1,0,null,0,null," ",
                " ",true,null,null);
    }

}
