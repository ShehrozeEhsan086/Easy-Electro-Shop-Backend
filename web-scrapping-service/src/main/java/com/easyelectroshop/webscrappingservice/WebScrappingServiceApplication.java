package com.easyelectroshop.webscrappingservice;

import com.easyelectroshop.webscrappingservice.Model.WebScrapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@EnableDiscoveryClient
public class WebScrappingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebScrappingServiceApplication.class, args);
    }


    @Bean
    @LoadBalanced
    WebClient.Builder getWebClientBuilder(){
        return WebClient.builder();
    }


    @Bean
    WebScrapper webScrapper(){
        return new WebScrapper();
    }

    @Bean
    List<WebScrapper> webScrapperList(){
        return new ArrayList<WebScrapper>();
    }

}