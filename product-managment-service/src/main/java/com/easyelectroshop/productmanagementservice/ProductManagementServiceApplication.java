package com.easyelectroshop.productmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductManagementServiceApplication.class, args);
    }

    @Bean
    public Date date() {
        return new Date();
    }


    @Bean
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("dd-MM-yyy");
    }

}
